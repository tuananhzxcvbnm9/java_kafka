create table if not exists orders (
  id uuid primary key,
  customer_id uuid not null,
  status varchar(32) not null,
  total_amount numeric(19,4) not null,
  version bigint not null default 0,
  created_at timestamptz not null,
  updated_at timestamptz
);

create index if not exists idx_orders_customer_created on orders(customer_id, created_at desc);
create index if not exists idx_orders_status_created on orders(status, created_at desc);

create table if not exists outbox_events (
  id uuid primary key,
  aggregate_type varchar(64) not null,
  aggregate_id varchar(64) not null,
  event_type varchar(128) not null,
  event_version int not null,
  payload_json text not null,
  status varchar(16) not null,
  created_at timestamptz not null,
  published_at timestamptz
);

create index if not exists idx_outbox_status_created on outbox_events(status, created_at);

create table if not exists processed_messages (
  id bigserial primary key,
  consumer_name varchar(128) not null,
  message_id varchar(128) not null,
  processed_at timestamptz not null default now(),
  unique(consumer_name, message_id)
);
