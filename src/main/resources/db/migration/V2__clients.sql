create table clients (
    id                      uuid primary key,
    organization_id         uuid         not null references organizations (id),
    name                    varchar(255) not null,
    company_name            varchar(255),
    industry                varchar(128),
    email                   varchar(255),
    phone                   varchar(64),
    address                 varchar(512),
    country                 varchar(128),
    website                 varchar(255),
    client_type             varchar(32)  not null,
    status                  varchar(32)  not null,
    account_manager_user_id uuid references users (id),
    contract_start_date     date,
    contract_end_date       date,
    contract_value          numeric(15, 2),
    payment_terms           varchar(128),
    notes                   text,
    archived                boolean      not null default false,
    created_at              timestamptz  not null,
    updated_at              timestamptz  not null
);

create index idx_clients_organization on clients (organization_id);
create index idx_clients_account_manager on clients (account_manager_user_id);
