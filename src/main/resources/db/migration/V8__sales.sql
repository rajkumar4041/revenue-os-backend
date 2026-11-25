create table leads (
    id                  uuid primary key,
    organization_id     uuid         not null references organizations (id),
    name                varchar(255) not null,
    company             varchar(255),
    email               varchar(255),
    phone               varchar(64),
    source              varchar(128),
    assigned_user_id    uuid references users (id),
    estimated_value     numeric(15, 2),
    expected_close_date date,
    notes               text,
    status              varchar(32)  not null,
    created_at          timestamptz  not null,
    updated_at          timestamptz  not null
);

create table opportunities (
    id                  uuid primary key,
    organization_id     uuid         not null references organizations (id),
    name                varchar(255) not null,
    lead_id             uuid references leads (id),
    client_id           uuid references clients (id),
    owner_user_id       uuid references users (id),
    stage               varchar(32)  not null,
    value               numeric(15, 2),
    expected_close_date date,
    probability         integer,
    notes               text,
    created_at          timestamptz  not null,
    updated_at          timestamptz  not null
);

create index idx_leads_organization on leads (organization_id);
create index idx_opportunities_organization on opportunities (organization_id);
