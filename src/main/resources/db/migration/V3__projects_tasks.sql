create table projects (
    id               uuid primary key,
    organization_id  uuid         not null references organizations (id),
    name             varchar(255) not null,
    code             varchar(64)  not null,
    client_id        uuid references clients (id),
    description      text,
    manager_user_id  uuid references users (id),
    start_date       date,
    end_date         date,
    estimated_budget numeric(15, 2),
    contract_value   numeric(15, 2),
    billing_type     varchar(32)  not null,
    hourly_rate      numeric(10, 2),
    status           varchar(32)  not null,
    priority         varchar(32)  not null,
    created_at       timestamptz  not null,
    updated_at       timestamptz  not null,
    constraint uk_projects_org_code unique (organization_id, code)
);

create table project_members (
    project_id uuid not null references projects (id) on delete cascade,
    user_id    uuid not null references users (id),
    primary key (project_id, user_id)
);

create table tasks (
    id                 uuid primary key,
    organization_id    uuid         not null references organizations (id),
    project_id         uuid         not null references projects (id),
    title              varchar(255) not null,
    description        text,
    assignee_user_id   uuid references users (id),
    created_by_user_id uuid references users (id),
    priority           varchar(32)  not null,
    status             varchar(32)  not null,
    start_date         date,
    due_date           date,
    estimated_hours    numeric(8, 2),
    actual_hours       numeric(8, 2),
    created_at         timestamptz  not null,
    updated_at         timestamptz  not null
);

create index idx_projects_organization on projects (organization_id);
create index idx_projects_client on projects (client_id);
create index idx_tasks_organization on tasks (organization_id);
create index idx_tasks_project on tasks (project_id);
create index idx_tasks_assignee on tasks (assignee_user_id);
