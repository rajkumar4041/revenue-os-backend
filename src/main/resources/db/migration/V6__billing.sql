create table invoices (
    id              uuid primary key,
    organization_id uuid          not null references organizations (id),
    invoice_number  varchar(32)   not null,
    client_id       uuid          not null references clients (id),
    project_id      uuid references projects (id),
    issue_date      date          not null,
    due_date        date,
    status          varchar(32)   not null,
    currency        varchar(8)    not null,
    subtotal        numeric(15, 2) not null default 0,
    tax_amount      numeric(15, 2) not null default 0,
    total           numeric(15, 2) not null default 0,
    notes           text,
    created_at      timestamptz   not null,
    updated_at      timestamptz   not null,
    constraint uk_invoices_org_number unique (organization_id, invoice_number)
);

create table invoice_items (
    id              uuid primary key,
    organization_id uuid           not null references organizations (id),
    invoice_id      uuid           not null references invoices (id) on delete cascade,
    description     varchar(512)   not null,
    quantity        numeric(12, 2) not null default 1,
    unit_price      numeric(15, 2) not null default 0,
    amount          numeric(15, 2) not null default 0,
    created_at      timestamptz    not null,
    updated_at      timestamptz    not null
);

create table payments (
    id              uuid primary key,
    organization_id uuid           not null references organizations (id),
    invoice_id      uuid           not null references invoices (id) on delete cascade,
    amount          numeric(15, 2) not null,
    paid_on         date           not null,
    method          varchar(32)    not null,
    reference       varchar(255),
    created_at      timestamptz    not null,
    updated_at      timestamptz    not null
);

create index idx_invoices_organization on invoices (organization_id);
create index idx_invoices_client on invoices (client_id);
create index idx_invoice_items_invoice on invoice_items (invoice_id);
create index idx_payments_invoice on payments (invoice_id);
create index idx_payments_organization on payments (organization_id);
