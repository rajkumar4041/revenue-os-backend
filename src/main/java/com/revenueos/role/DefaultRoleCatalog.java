package com.revenueos.role;

import com.revenueos.permission.Permissions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The roles every new organization starts with, and the permissions each one
 * grants. These are seeded as {@code system} roles at company registration;
 * admins can add their own on top.
 */
public final class DefaultRoleCatalog {

    public static final String COMPANY_ADMIN = "COMPANY_ADMIN";

    private DefaultRoleCatalog() {
    }

    private static final Map<String, Set<String>> ROLES = new LinkedHashMap<>();

    static {
        ROLES.put(COMPANY_ADMIN, Set.copyOf(Permissions.ALL));
        ROLES.put("MANAGER", Set.of(
                Permissions.CLIENT_VIEW,
                Permissions.PROJECT_VIEW, Permissions.PROJECT_CREATE, Permissions.PROJECT_UPDATE,
                Permissions.TASK_VIEW, Permissions.TASK_CREATE, Permissions.TASK_UPDATE, Permissions.TASK_ASSIGN,
                Permissions.TIMESHEET_APPROVE,
                Permissions.EMPLOYEE_VIEW,
                Permissions.REPORT_VIEW));
        ROLES.put("SALES", Set.of(
                Permissions.CLIENT_VIEW, Permissions.CLIENT_CREATE, Permissions.CLIENT_UPDATE,
                Permissions.REPORT_VIEW));
        ROLES.put("ACCOUNT_MANAGER", Set.of(
                Permissions.CLIENT_VIEW, Permissions.CLIENT_UPDATE,
                Permissions.INVOICE_VIEW, Permissions.INVOICE_CREATE,
                Permissions.REVENUE_VIEW));
        ROLES.put("EMPLOYEE", Set.of(
                Permissions.TASK_VIEW, Permissions.TASK_UPDATE,
                Permissions.TIMESHEET_SUBMIT));
        ROLES.put("FINANCE", Set.of(
                Permissions.INVOICE_VIEW, Permissions.INVOICE_CREATE,
                Permissions.REVENUE_VIEW, Permissions.REVENUE_MANAGE,
                Permissions.REPORT_VIEW));
    }

    public static Map<String, Set<String>> all() {
        return ROLES;
    }
}
