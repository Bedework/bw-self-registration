
    drop table if exists bw_accounts cascade;

    drop table if exists bw_roles cascade;

    drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start 1 increment 1;

    create table bw_accounts (
        bw_id int8 not null,
        bw_seq int4 not null,
        bw_confid varchar(100) not null,
        bw_account varchar(250) not null,
        bw_enabled char(1) not null,
        bw_dtstamp varchar(50) not null,
        bw_fname varchar(100) not null,
        bw_lname varchar(100),
        bw_email varchar(300) not null,
        bw_pw varchar(300) not null,
        bw_props text,
        primary key (bw_id)
    );

    create table bw_roles (
        bw_id int8 not null,
        bw_seq int4 not null,
        bw_account varchar(250) not null,
        bw_role varchar(300) not null,
        primary key (bw_id)
    );
create index bwidx_account on bw_accounts (bw_account);

    alter table bw_accounts 
        add constraint UK_ofhroudh14ijnsq4cd3eks93l unique (bw_account);
create index bwidx_raccount on bw_roles (bw_account);

    alter table bw_roles 
        add constraint UK_sj9b2epfijljpvtyl24fxa9qs unique (bw_account);
