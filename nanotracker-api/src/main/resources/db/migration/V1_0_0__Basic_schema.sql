create schema if not exists user_management;
create schema if not exists task_management;

create table user_management.t_user
(
    id         serial primary key,
    c_email    varchar not null check ( length(trim(c_username)) > 0 ) unique,
    c_username varchar not null check ( length(trim(c_username)) > 0 ) unique,
    c_password varchar not null check ( length(trim(c_password)) > 8 )
);

create table user_management.t_authority
(
    id          serial primary key,
    c_authority varchar not null check ( length(trim(c_authority)) > 0 ) unique
);

create table user_management.t_user_authority
(
    id           serial primary key,
    id_user      int not null references user_management.t_user (id),
    id_authority int not null references user_management.t_authority (id),
    constraint uk_user_authority unique (id_user, id_authority)
);

create table task_management.t_task
(
    id               serial primary key,
    c_title          varchar(100)  not null check (length(trim(c_title)) >= 3),
    c_details        varchar(5000) not null,
    c_creation_date  timestamp     not null,
    c_completed      boolean       not null,
    c_completed_date timestamp,
    id_user          int           not null references user_management.t_user (id)
);