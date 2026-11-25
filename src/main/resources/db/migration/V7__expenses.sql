create table expenses (
    id                   uuid primary key,
    organization_id      uuid           not null references organizations (id),
    project_id           uuid references projects (id),
    category             varchar(32)    not null,
    amount               numeric(15, 2) not null,
    incurred_on          date           not null,
    description          text,
    status               varchar(32)    not null,
    recorded_by_user_id  uuid references users (id),
    created_at           timestamptz    not null,
    updated_at           timestamptz    not null
);

create index idx_expenses_organization on expenses (organization_id);
create index idx_expenses_project on expenses (project_id);
