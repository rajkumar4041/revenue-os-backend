package com.revenueos.permission;

import java.util.List;

/**
 * Canonical list of permission codes. Kept in one place so controllers,
 * seed data, and tests all refer to the same strings.
 */
public final class Permissions {

    private Permissions() {
    }

    public static final String CLIENT_VIEW = "CLIENT_VIEW";
    public static final String CLIENT_CREATE = "CLIENT_CREATE";
    public static final String CLIENT_UPDATE = "CLIENT_UPDATE";
    public static final String CLIENT_DELETE = "CLIENT_DELETE";

    public static final String PROJECT_VIEW = "PROJECT_VIEW";
    public static final String PROJECT_CREATE = "PROJECT_CREATE";
    public static final String PROJECT_UPDATE = "PROJECT_UPDATE";
    public static final String PROJECT_DELETE = "PROJECT_DELETE";

    public static final String TASK_VIEW = "TASK_VIEW";
    public static final String TASK_CREATE = "TASK_CREATE";
    public static final String TASK_UPDATE = "TASK_UPDATE";
    public static final String TASK_ASSIGN = "TASK_ASSIGN";

    public static final String TIMESHEET_SUBMIT = "TIMESHEET_SUBMIT";
    public static final String TIMESHEET_APPROVE = "TIMESHEET_APPROVE";

    public static final String REVENUE_VIEW = "REVENUE_VIEW";
    public static final String REVENUE_MANAGE = "REVENUE_MANAGE";

    public static final String INVOICE_VIEW = "INVOICE_VIEW";
    public static final String INVOICE_CREATE = "INVOICE_CREATE";

    public static final String EMPLOYEE_VIEW = "EMPLOYEE_VIEW";
    public static final String EMPLOYEE_MANAGE = "EMPLOYEE_MANAGE";

    public static final String REPORT_VIEW = "REPORT_VIEW";

    public static final String USER_MANAGE = "USER_MANAGE";
    public static final String ROLE_MANAGE = "ROLE_MANAGE";
    public static final String ORGANIZATION_MANAGE = "ORGANIZATION_MANAGE";

    /** Full catalog, used by tests and documentation. */
    public static final List<String> ALL = List.of(
            CLIENT_VIEW, CLIENT_CREATE, CLIENT_UPDATE, CLIENT_DELETE,
            PROJECT_VIEW, PROJECT_CREATE, PROJECT_UPDATE, PROJECT_DELETE,
            TASK_VIEW, TASK_CREATE, TASK_UPDATE, TASK_ASSIGN,
            TIMESHEET_SUBMIT, TIMESHEET_APPROVE,
            REVENUE_VIEW, REVENUE_MANAGE,
            INVOICE_VIEW, INVOICE_CREATE,
            EMPLOYEE_VIEW, EMPLOYEE_MANAGE,
            REPORT_VIEW,
            USER_MANAGE, ROLE_MANAGE, ORGANIZATION_MANAGE);
}
