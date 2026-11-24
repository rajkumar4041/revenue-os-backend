-- Identity, tenancy and RBAC foundation for RevenueOS.

create table organizations (
    id                uuid primary key,
    name              varchar(255) not null,
    slug              varchar(255) not null,
    status            varchar(32)  not null,
    subscription_plan varchar(32)  not null,
    created_at        timestamptz  not null,
    updated_at        timestamptz  not null,
    constraint uk_organizations_slug unique (slug)
);

create table permissions (
    id          uuid primary key,
    code        varchar(64)  not null,
    category    varchar(64)  not null,
    description varchar(255),
    created_at  timestamptz  not null,
    updated_at  timestamptz  not null,
    constraint uk_permissions_code unique (code)
);

create table roles (
    id              uuid primary key,
    organization_id uuid         not null references organizations (id),
    name            varchar(64)  not null,
    description     varchar(255),
    is_system       boolean      not null default false,
    created_at      timestamptz  not null,
    updated_at      timestamptz  not null,
    constraint uk_roles_org_name unique (organization_id, name)
);

create table role_permissions (
    role_id       uuid not null references roles (id) on delete cascade,
    permission_id uuid not null references permissions (id) on delete cascade,
    primary key (role_id, permission_id)
);

create table users (
    id              uuid primary key,
    organization_id uuid         not null references organizations (id),
    name            varchar(255) not null,
    email           varchar(255) not null,
    password_hash   varchar(255) not null,
    status          varchar(32)  not null,
    platform_admin  boolean      not null default false,
    created_at      timestamptz  not null,
    updated_at      timestamptz  not null,
    constraint uk_users_email unique (email)
);

create table user_roles (
    user_id uuid not null references users (id) on delete cascade,
    role_id uuid not null references roles (id) on delete cascade,
    primary key (user_id, role_id)
);

create table departments (
    id              uuid primary key,
    organization_id uuid         not null references organizations (id),
    name            varchar(255) not null,
    manager_user_id uuid references users (id),
    created_at      timestamptz  not null,
    updated_at      timestamptz  not null,
    constraint uk_departments_org_name unique (organization_id, name)
);

create table refresh_tokens (
    id         uuid primary key,
    user_id    uuid         not null references users (id) on delete cascade,
    token_hash varchar(255) not null,
    expires_at timestamptz  not null,
    revoked    boolean      not null default false,
    created_at timestamptz  not null,
    updated_at timestamptz  not null,
    constraint uk_refresh_tokens_hash unique (token_hash)
);

create table password_reset_tokens (
    id         uuid primary key,
    user_id    uuid         not null references users (id) on delete cascade,
    token_hash varchar(255) not null,
    expires_at timestamptz  not null,
    used       boolean      not null default false,
    created_at timestamptz  not null,
    updated_at timestamptz  not null,
    constraint uk_password_reset_tokens_hash unique (token_hash)
);

create table audit_logs (
    id              uuid primary key,
    organization_id uuid,
    user_id         uuid,
    action          varchar(128) not null,
    entity_type     varchar(128),
    entity_id       uuid,
    metadata        text,
    created_at      timestamptz  not null,
    updated_at      timestamptz  not null
);

create index idx_users_organization on users (organization_id);
create index idx_roles_organization on roles (organization_id);
create index idx_departments_organization on departments (organization_id);
create index idx_refresh_tokens_user on refresh_tokens (user_id);
create index idx_password_reset_tokens_user on password_reset_tokens (user_id);
create index idx_audit_logs_organization on audit_logs (organization_id);

-- Permission catalog. Roles and the demo tenant are created by the application
-- registration flow so passwords and per-org defaults are produced consistently.
insert into permissions (id, code, category, description, created_at, updated_at) values
    (gen_random_uuid(), 'CLIENT_VIEW',         'Clients',      'View clients',                now(), now()),
    (gen_random_uuid(), 'CLIENT_CREATE',       'Clients',      'Create clients',              now(), now()),
    (gen_random_uuid(), 'CLIENT_UPDATE',       'Clients',      'Update clients',              now(), now()),
    (gen_random_uuid(), 'CLIENT_DELETE',       'Clients',      'Delete or archive clients',   now(), now()),
    (gen_random_uuid(), 'PROJECT_VIEW',        'Projects',     'View projects',               now(), now()),
    (gen_random_uuid(), 'PROJECT_CREATE',      'Projects',     'Create projects',             now(), now()),
    (gen_random_uuid(), 'PROJECT_UPDATE',      'Projects',     'Update projects',             now(), now()),
    (gen_random_uuid(), 'PROJECT_DELETE',      'Projects',     'Delete projects',             now(), now()),
    (gen_random_uuid(), 'TASK_VIEW',           'Tasks',        'View tasks',                  now(), now()),
    (gen_random_uuid(), 'TASK_CREATE',         'Tasks',        'Create tasks',                now(), now()),
    (gen_random_uuid(), 'TASK_UPDATE',         'Tasks',        'Update tasks',                now(), now()),
    (gen_random_uuid(), 'TASK_ASSIGN',         'Tasks',        'Assign tasks to employees',   now(), now()),
    (gen_random_uuid(), 'TIMESHEET_SUBMIT',    'Timesheets',   'Submit timesheets',           now(), now()),
    (gen_random_uuid(), 'TIMESHEET_APPROVE',   'Timesheets',   'Approve or reject timesheets', now(), now()),
    (gen_random_uuid(), 'REVENUE_VIEW',        'Revenue',      'View revenue',                now(), now()),
    (gen_random_uuid(), 'REVENUE_MANAGE',      'Revenue',      'Manage revenue',              now(), now()),
    (gen_random_uuid(), 'INVOICE_VIEW',        'Invoicing',    'View invoices',               now(), now()),
    (gen_random_uuid(), 'INVOICE_CREATE',      'Invoicing',    'Create invoices',             now(), now()),
    (gen_random_uuid(), 'EMPLOYEE_VIEW',       'Employees',    'View employees',              now(), now()),
    (gen_random_uuid(), 'EMPLOYEE_MANAGE',     'Employees',    'Manage employees',            now(), now()),
    (gen_random_uuid(), 'REPORT_VIEW',         'Reports',      'View reports',                now(), now()),
    (gen_random_uuid(), 'USER_MANAGE',         'Administration', 'Manage users',              now(), now()),
    (gen_random_uuid(), 'ROLE_MANAGE',         'Administration', 'Manage roles and permissions', now(), now()),
    (gen_random_uuid(), 'ORGANIZATION_MANAGE', 'Administration', 'Manage organization settings', now(), now());
