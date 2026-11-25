create table timesheets (
    id                  uuid primary key,
    organization_id     uuid        not null references organizations (id),
    user_id             uuid        not null references users (id),
    period_start        date        not null,
    period_end          date        not null,
    status              varchar(32) not null,
    submitted_at        timestamptz,
    approved_by_user_id uuid references users (id),
    approved_at         timestamptz,
    review_note         varchar(512),
    created_at          timestamptz not null,
    updated_at          timestamptz not null,
    constraint uk_timesheets_user_period unique (user_id, period_start)
);

create table time_entries (
    id              uuid primary key,
    organization_id uuid          not null references organizations (id),
    timesheet_id    uuid          not null references timesheets (id) on delete cascade,
    project_id      uuid          not null references projects (id),
    task_id         uuid references tasks (id),
    work_date       date          not null,
    hours           numeric(6, 2) not null default 0,
    billable        boolean       not null default true,
    description     text,
    started_at      timestamptz,
    running         boolean       not null default false,
    created_at      timestamptz   not null,
    updated_at      timestamptz   not null
);

create index idx_timesheets_organization on timesheets (organization_id);
create index idx_timesheets_user on timesheets (user_id);
create index idx_time_entries_timesheet on time_entries (timesheet_id);
create index idx_time_entries_organization on time_entries (organization_id);
