#!/usr/bin/env bash
#
# End-to-end API smoke test + demo-data seeder for RevenueOS.
#
# Logs in (or registers the demo company if it doesn't exist yet), then drives
# every REST endpoint in dependency order — creating clients, projects, tasks,
# employees, invoices, timesheets, expenses and sales records along the way.
# Each call asserts an expected HTTP status; a summary + non-zero exit code is
# produced if anything regresses.
#
# Usage:
#   BASE_URL=https://api.yourhost.com ./scripts/smoke-test.sh
#   ./scripts/smoke-test.sh https://api.yourhost.com
#
# Requires: bash, curl, jq
set -uo pipefail

BASE_URL="${1:-${BASE_URL:-http://localhost:8080}}"
API="${BASE_URL%/}/api/v1"
EMAIL="${DEMO_EMAIL:-admin@demo.io}"
PASSWORD="${DEMO_PASSWORD:-Password123!}"
TODAY="$(date +%F)"
NEXT_MONTH="$(date -d '+30 days' +%F 2>/dev/null || date -v+30d +%F)"

command -v jq >/dev/null 2>&1 || { echo "jq is required (sudo apt install jq / brew install jq)"; exit 1; }

PASS=0; FAIL=0; ACCESS=""; BODY=""
declare -a FAILURES=()

# req METHOD PATH EXPECTED_CODES [JSON_BODY]
# Sends an (optionally authenticated) request, stores the response body in $BODY,
# and records pass/fail against the space-separated list of acceptable codes.
req() {
  local method="$1" path="$2" expected="$3" data="${4:-}"
  local url="$API$path" code
  local args=(-s -o /tmp/ros_body -w '%{http_code}' -X "$method" "$url"
              -H 'Content-Type: application/json')
  [ -n "$ACCESS" ] && args+=(-H "Authorization: Bearer $ACCESS")
  [ -n "$data" ] && args+=(-d "$data")
  code="$(curl "${args[@]}")"
  BODY="$(cat /tmp/ros_body)"
  if [[ " $expected " == *" $code "* ]]; then
    printf '  \033[32mok\033[0m  %-6s %-40s -> %s\n' "$method" "$path" "$code"
    PASS=$((PASS+1))
  else
    printf '  \033[31mFAIL\033[0m %-6s %-40s -> %s (want: %s)\n' "$method" "$path" "$code" "$expected"
    printf '       %s\n' "$(echo "$BODY" | head -c 300)"
    FAIL=$((FAIL+1)); FAILURES+=("$method $path -> $code")
  fi
}
jqv() { echo "$BODY" | jq -r "$1" 2>/dev/null; }
section() { printf '\n\033[1m== %s ==\033[0m\n' "$1"; }

echo "RevenueOS smoke test against: $API"

# ---------------------------------------------------------------------------
section "AUTH"
# Prefer login; if the demo company isn't seeded yet, register it first.
req POST /auth/login "200 401 400" "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}"
if [ "$FAIL" -ne 0 ] || [ -z "$(jqv '.accessToken')" ] || [ "$(jqv '.accessToken')" = "null" ]; then
  echo "  (login failed — registering demo company)"
  FAIL=0; FAILURES=()
  req POST /auth/register-company "200 201 409" \
    "{\"companyName\":\"Demo Company\",\"adminName\":\"Demo Admin\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}"
  req POST /auth/login "200" "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}"
fi
ACCESS="$(jqv '.accessToken')"
REFRESH="$(jqv '.refreshToken')"
ORG_ID="$(jqv '.user.organizationId')"
USER_ID="$(jqv '.user.id')"
[ -z "$ACCESS" ] || [ "$ACCESS" = "null" ] && { echo "FATAL: no access token, aborting"; exit 1; }

req GET  /auth/me "200"
req POST /auth/refresh "200" "{\"refreshToken\":\"$REFRESH\"}"
ACCESS="$(jqv '.accessToken')"   # adopt rotated token

# ---------------------------------------------------------------------------
section "ORGANIZATION / RBAC"
req GET /organizations/$ORG_ID "200"
req PUT /organizations/$ORG_ID "200" '{"name":"Demo Company"}'
req GET /permissions "200"
req GET /roles "200"
req POST /roles "200 201" '{"name":"QA Reviewer","description":"smoke role","permissionCodes":["TASK_VIEW","PROJECT_VIEW"]}'
ROLE_ID="$(jqv '.id')"
req PUT /roles/$ROLE_ID "200" '{"name":"QA Reviewer","description":"updated","permissionCodes":["TASK_VIEW"]}'

# ---------------------------------------------------------------------------
section "USERS"
req GET  /users "200"
req POST /users "200 201" "{\"name\":\"Priya Sharma\",\"email\":\"priya+$(date +%s)@demo.io\",\"password\":\"Password123!\",\"roleIds\":[]}"
NEW_USER_ID="$(jqv '.id')"
req GET  /users/$NEW_USER_ID "200"
req PUT  /users/$NEW_USER_ID "200" '{"name":"Priya S.","status":"ACTIVE"}'
req POST /users/$NEW_USER_ID/roles "200" "{\"roleIds\":[\"$ROLE_ID\"]}"

# ---------------------------------------------------------------------------
section "TEAMS & EMPLOYEES"
req GET  /teams "200"
TEAM_NAME="Engineering $(date +%s)"   # unique per run so re-runs don't 409
req POST /teams "200 201" "{\"name\":\"$TEAM_NAME\",\"managerUserId\":\"$USER_ID\"}"
TEAM_ID="$(jqv '.id')"
req PUT  /teams/$TEAM_ID "200" "{\"name\":\"$TEAM_NAME\",\"managerUserId\":\"$USER_ID\"}"
req GET  /employees "200"
req POST /employees "200 201" "{\"fullName\":\"Amit Verma\",\"email\":\"amit+$(date +%s)@demo.io\",\"teamId\":\"$TEAM_ID\",\"designation\":\"Senior Engineer\",\"employmentType\":\"FULL_TIME\",\"costRate\":40,\"billableRate\":90,\"hireDate\":\"$TODAY\"}"
EMP_ID="$(jqv '.id')"
req GET   /employees/$EMP_ID "200"
EMP_EMAIL="$(jqv '.email')"
req PUT   /employees/$EMP_ID "200" "{\"fullName\":\"Amit Verma\",\"email\":\"$EMP_EMAIL\",\"teamId\":\"$TEAM_ID\",\"designation\":\"Lead Engineer\",\"employmentType\":\"FULL_TIME\",\"costRate\":45,\"billableRate\":100}"

# ---------------------------------------------------------------------------
section "CLIENTS"
req GET  /clients "200"
req POST /clients "200 201" '{"name":"Acme Corp","companyName":"Acme Corporation","industry":"Manufacturing","email":"ap@acme.test","phone":"+1-202-555-0100","country":"USA","clientType":"DIRECT","status":"ACTIVE","contractValue":250000,"paymentTerms":"NET30"}'
CLIENT_ID="$(jqv '.id')"
req GET  /clients/$CLIENT_ID "200"
req PUT  /clients/$CLIENT_ID "200" '{"name":"Acme Corp","companyName":"Acme Corporation","industry":"Manufacturing","clientType":"DIRECT","status":"ACTIVE","contractValue":300000}'

# ---------------------------------------------------------------------------
section "PROJECTS & TASKS"
req POST /projects "200 201" "{\"name\":\"Website Revamp\",\"code\":\"WEB-$(date +%s)\",\"clientId\":\"$CLIENT_ID\",\"description\":\"Marketing site rebuild\",\"managerUserId\":\"$USER_ID\",\"startDate\":\"$TODAY\",\"endDate\":\"$NEXT_MONTH\",\"estimatedBudget\":50000,\"contractValue\":60000,\"billingType\":\"FIXED\",\"status\":\"ACTIVE\",\"priority\":\"HIGH\",\"memberUserIds\":[\"$USER_ID\"]}"
PROJECT_ID="$(jqv '.id')"
req GET  /projects "200"
req GET  /projects/$PROJECT_ID "200"
req PUT  /projects/$PROJECT_ID "200" "{\"name\":\"Website Revamp\",\"code\":\"WEB-01\",\"clientId\":\"$CLIENT_ID\",\"billingType\":\"FIXED\",\"status\":\"ACTIVE\",\"priority\":\"MEDIUM\"}"
req POST /tasks "200 201" "{\"projectId\":\"$PROJECT_ID\",\"title\":\"Design homepage\",\"description\":\"Hero + nav\",\"assigneeUserId\":\"$USER_ID\",\"priority\":\"HIGH\",\"status\":\"TODO\",\"startDate\":\"$TODAY\",\"dueDate\":\"$NEXT_MONTH\",\"estimatedHours\":16}"
TASK_ID="$(jqv '.id')"
req GET   /tasks "200"
req GET   /tasks/my-tasks "200"
req GET   /tasks/$TASK_ID "200"
req PUT   /tasks/$TASK_ID "200" "{\"projectId\":\"$PROJECT_ID\",\"title\":\"Design homepage v2\",\"priority\":\"HIGH\",\"status\":\"IN_PROGRESS\"}"
req PATCH /tasks/$TASK_ID/status "200" '{"status":"IN_REVIEW"}'

# ---------------------------------------------------------------------------
section "TIMESHEETS & TIMER"
# A timesheet is singular per user per week, so this flow is only fully
# exercisable while the current-week sheet is still a draft. On re-runs within
# the same week the sheet is already approved — we then only verify the reads
# and treat the locked mutations as skipped (the week is already covered).
req POST /timesheets/current "200 201"
TS_ID="$(jqv '.timesheet.id')"
TS_STATUS="$(jqv '.timesheet.status')"
req GET  /timesheets/$TS_ID "200"
req GET  /timesheets/my "200"
req GET  /timesheets/pending "200"
if [ "$TS_STATUS" = "DRAFT" ]; then
  # throwaway entry to exercise add + delete on a draft
  req POST   /timesheets/$TS_ID/entries "200 201" "{\"projectId\":\"$PROJECT_ID\",\"taskId\":\"$TASK_ID\",\"workDate\":\"$TODAY\",\"hours\":2,\"billable\":false,\"description\":\"scratch\"}"
  TMP_ENTRY="$(jqv '.id')"
  req DELETE /timesheets/entries/$TMP_ENTRY "200 204"
  # real entry that stays on the sheet through submit + approve
  req POST   /timesheets/$TS_ID/entries "200 201" "{\"projectId\":\"$PROJECT_ID\",\"taskId\":\"$TASK_ID\",\"workDate\":\"$TODAY\",\"hours\":6,\"billable\":true,\"description\":\"Homepage design\"}"
  req POST   /timesheets/$TS_ID/submit "200"
  req POST   /timesheets/$TS_ID/approve "200"
else
  echo "  (current timesheet is $TS_STATUS — skipping add/submit/approve for this week)"
fi
# timer: ensure clean state, then start/active/stop
req POST /timer/stop "200 400 404 409"
req POST /timer/start "200 201" "{\"projectId\":\"$PROJECT_ID\",\"taskId\":\"$TASK_ID\",\"description\":\"Focus block\"}"
req GET  /timer/active "200"
req POST /timer/stop "200"

# ---------------------------------------------------------------------------
section "INVOICES & PAYMENTS"
req POST /invoices "200 201" "{\"clientId\":\"$CLIENT_ID\",\"projectId\":\"$PROJECT_ID\",\"issueDate\":\"$TODAY\",\"dueDate\":\"$NEXT_MONTH\",\"currency\":\"USD\",\"taxAmount\":0,\"notes\":\"Milestone 1\",\"items\":[{\"description\":\"Design phase\",\"quantity\":10,\"unitPrice\":100},{\"description\":\"Dev phase\",\"quantity\":20,\"unitPrice\":120}]}"
INVOICE_ID="$(jqv '.invoice.id')"   # detail response nests under .invoice
req GET  /invoices "200"
req GET  /invoices/$INVOICE_ID "200"
req POST /invoices/$INVOICE_ID/send "200"
req POST /invoices/$INVOICE_ID/payments "200 201" "{\"amount\":500,\"paidOn\":\"$TODAY\",\"method\":\"BANK_TRANSFER\",\"reference\":\"TXN-123\"}"

# ---------------------------------------------------------------------------
section "EXPENSES"
req POST /expenses "200 201" "{\"projectId\":\"$PROJECT_ID\",\"category\":\"SOFTWARE\",\"amount\":199.99,\"incurredOn\":\"$TODAY\",\"description\":\"Figma seats\"}"
EXPENSE_ID="$(jqv '.id')"
req GET  /expenses "200"
req PUT  /expenses/$EXPENSE_ID "200" "{\"projectId\":\"$PROJECT_ID\",\"category\":\"SOFTWARE\",\"amount\":249.99,\"incurredOn\":\"$TODAY\",\"description\":\"Figma seats (annual)\"}"
req POST /expenses/$EXPENSE_ID/approve "200"

# ---------------------------------------------------------------------------
section "SALES — LEADS & OPPORTUNITIES"
req POST /leads "200 201" "{\"name\":\"Globex Inc\",\"company\":\"Globex\",\"email\":\"cto@globex.test\",\"source\":\"Referral\",\"estimatedValue\":80000,\"expectedCloseDate\":\"$NEXT_MONTH\",\"status\":\"QUALIFIED\"}"
LEAD_ID="$(jqv '.id')"
req GET  /leads "200"
req PUT  /leads/$LEAD_ID "200" "{\"name\":\"Globex Inc\",\"company\":\"Globex\",\"estimatedValue\":90000,\"status\":\"QUALIFIED\"}"
req POST /leads/$LEAD_ID/convert "200 201"
OPP_ID="$(jqv '.id')"
req GET   /opportunities "200"
if [ -z "$OPP_ID" ] || [ "$OPP_ID" = "null" ]; then
  req POST /opportunities "200 201" "{\"name\":\"Globex deal\",\"clientId\":\"$CLIENT_ID\",\"ownerUserId\":\"$USER_ID\",\"stage\":\"PROPOSAL\",\"value\":90000,\"expectedCloseDate\":\"$NEXT_MONTH\",\"probability\":60}"
  OPP_ID="$(jqv '.id')"
fi
req PATCH /opportunities/$OPP_ID/stage "200" '{"stage":"NEGOTIATION"}'

# ---------------------------------------------------------------------------
section "REVENUE & AUDIT"
req GET /revenue/summary "200"
req GET "/revenue/monthly?months=6" "200"
req GET /audit "200"

# ---------------------------------------------------------------------------
section "CLEANUP (destructive endpoints)"
# 409 is valid here: the timesheet was approved above, and the service correctly
# refuses edits to a non-draft sheet — so any of these codes means the guard works.
req POST   /clients/$CLIENT_ID/archive "200"
req DELETE /roles/$ROLE_ID "200 204"

# ---------------------------------------------------------------------------
printf '\n\033[1m== SUMMARY ==\033[0m\n'
printf 'Passed: \033[32m%s\033[0m   Failed: \033[31m%s\033[0m\n' "$PASS" "$FAIL"
if [ "$FAIL" -ne 0 ]; then
  printf '\nFailures:\n'
  printf '  - %s\n' "${FAILURES[@]}"
  exit 1
fi
echo "All endpoints healthy ✅"
