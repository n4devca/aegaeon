/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

create table client_type (
    id int(11) not null auto_increment,
    code varchar(40) character set latin1 collate latin1_bin not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

create table scope (
    id int(11) not null auto_increment,
    name varchar(40) not null,
    description varchar(500),
    issystem tinyint(1) not null default 0,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

-- ----------------------------------------------------------------------------
-- Client tables
-- ----------------------------------------------------------------------------

create table client (
    id int(11) not null auto_increment,
    public_id varchar(50) not null,
    secret varchar(1000) not null,
    name varchar(40) not null,
    description varchar(500),
    logourl varchar(300),
    client_type_id int(11) not null,
    provider_name varchar(40) not null,
    access_token_seconds int(11) not null default 3600,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table client add constraint cl_cltypeid_cltye_fk foreign key (client_type_id) references client_type(id);
alter table client add unique index cl_publicid_uq (public_id);

create table client_redirection (
    id int(11) not null auto_increment,
    url varchar(1000),
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table client_redirection add constraint clr_client_id_client_fk foreign key (client_id) references client(id) on delete cascade on update no action;

create table client_scope (
    id int(11) not null auto_increment,
    client_id int(11) not null,
    scope_id  int(11) not null,
    mandatory tinyint(1) not null default 0,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table client_scope add constraint csc_client_id_client_fk foreign key (client_id) references client(id) on delete cascade on update no action;
alter table client_scope add constraint csc_sc_id_sc_fk foreign key (scope_id) references scope(id) on delete cascade on update no action;

-- ----------------------------------------------------------------------------
-- User tables
-- ----------------------------------------------------------------------------

create table authority (
    id int(11) not null auto_increment,
    code varchar(50) character set latin1 collate latin1_bin not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table authority add unique index authority_code_uq (code);

create table users (
    id int(11) not null auto_increment,
    username varchar(100) not null,
    email varchar(100) not null,
    name varchar(100),
    uniqueIdentifier varchar(128)  character set latin1 collate latin1_bin not null,
    passwd CHAR(60) character set latin1 collate latin1_bin not null,
    enabled tinyint(1) not null default 0,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

create table user_authority (
    user_id int(11) not null,
    authority_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(user_id, authority_id)
);

alter table user_authority add constraint ua_user_id_user_fk foreign key (user_id) references users(id) on delete cascade on update no action;
alter table user_authority add constraint ua_auth_id_auth_fk foreign key (authority_id) references authority(id) on delete cascade on update no action;

-- ----------------------------------------------------------------------------
-- Tokens
-- ----------------------------------------------------------------------------

create table authorization_code (
    id int(11) not null auto_increment,
    code varchar(100) character set latin1 collate latin1_bin not null,
    validuntil datetime not null,
    user_id int(11) not null,
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table authorization_code add constraint athc_user_id_user_fk foreign key (user_id) references users(id) on delete cascade on update no action;
alter table authorization_code add constraint athc_client_id_client_fk foreign key (client_id) references client(id) on delete cascade on update no action;
alter table authorization_code add unique index athc_code_uq (code);

create table access_token (
    id int(11) not null auto_increment,
    token varchar(2000) character set latin1 collate latin1_bin not null,
    validuntil datetime not null,
    user_id int(11) not null,
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table access_token add constraint actk_user_id_user_fk foreign key (user_id) references users(id) on delete cascade on update no action;
alter table access_token add constraint actk_client_id_client_fk foreign key (client_id) references client(id) on delete cascade on update no action;

create table refresh_token (
    id int(11) not null auto_increment,
    token varchar(2000) character set latin1 collate latin1_bin not null,
    validuntil datetime not null,
    user_id int(11) not null,
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table refresh_token add constraint rftk_user_id_user_fk foreign key (user_id) references users(id) on delete cascade on update no action;
alter table refresh_token add constraint rftk_client_id_client_fk foreign key (client_id) references client(id) on delete cascade on update no action;
