create table employees (
    id                uuid primary key,
    organization_id   uuid         not null references organizations (id),
    full_name         varchar(255) not null,
    email             varchar(255) not null,
    user_id           uuid references users (id),
    department_id     uuid references departments (id),
    designation       varchar(128),
    employment_type   varchar(32)  not null,
    employment_status varchar(32)  not null,
    cost_rate         numeric(10, 2),
    billable_rate     numeric(10, 2),
    hire_date         date,
    end_date          date,
    created_at        timestamptz  not null,
    updated_at        timestamptz  not null
);

create index idx_employees_organization on employees (organization_id);
create index idx_employees_department on employees (department_id);
create index idx_employees_user on employees (user_id);
