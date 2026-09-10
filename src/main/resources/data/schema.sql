drop table if exists contact_tags;
create table contact_tags
(
    cont_id bigint not null,
    ctag_tx varchar(100)
);

drop table if exists contacts;
create table contacts
(
    cont_id           bigint not null,
    type_tx           varchar(10),

    cont_name_tx      varchar(100),
    cont_firs_name_tx varchar(100),
    cont_last_name_tx varchar(100),
    cont_disp_name_tx varchar(100),
    cont_iban_tx      varchar(100),
    cont_txid_tx      varchar(100),
    cont_job_tx       varchar(100),
    cont_slry_nm      decimal(10.2),

    addr_stre_tx      varchar(100),
    addr_post_tx      varchar(10),
    addr_city_tx      varchar(100),
    addr_cntr_tx      varchar(100),

    crea_dt           timestamp,
    updt_dt           timestamp,
    vers_nm           integer
);

create unique index cont_idx on contacts (cont_id);
alter table contacts
    add constraint cont_pk primary key (cont_id);

create sequence contacts_seq start with 1 increment by 1 cache 1;
