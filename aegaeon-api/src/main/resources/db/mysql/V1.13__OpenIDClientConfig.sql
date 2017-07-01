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
 
-- Replace by grant_type
drop table client_type;
 
alter table client drop grant_type;
alter table client add application_type varchar(45) character set latin1 collate latin1_bin not null default 'web';
alter table client add client_uri varchar(250);
alter table client add policy_uri varchar(250);
alter table client add tos_uri varchar(250);
alter table client add jwks_uri varchar(250);
alter table client add jwks varchar(4000) character set latin1 collate latin1_bin;
alter table client add sector_identifier_uri varchar(250);
alter table client add subject_type varchar(45);
alter table client add id_token_signed_response_alg varchar(45) character set latin1 collate latin1_bin not null default 'RS256';
alter table client add id_token_encrypted_response_alg varchar(45) character set latin1 collate latin1_bin;
alter table client add id_token_encrypted_response_enc varchar(45) character set latin1 collate latin1_bin not null default 'A128CBC-HS256';
alter table client add userinfo_signed_response_alg varchar(45) character set latin1 collate latin1_bin;
alter table client add userinfo_encrypted_response_alg varchar(45) character set latin1 collate latin1_bin;
alter table client add userinfo_encrypted_response_enc varchar(45) character set latin1 collate latin1_bin not null default 'A128CBC-HS256';
alter table client add request_object_signing_alg varchar(45) character set latin1 collate latin1_bin;
alter table client add request_object_encryption_alg varchar(45) character set latin1 collate latin1_bin;
alter table client add request_object_encryption_enc varchar(45) character set latin1 collate latin1_bin not null default 'A128CBC-HS256';
alter table client add token_endpoint_auth_method varchar(45) character set latin1 collate latin1_bin not null default 'client_secret_basic';
alter table client add token_endpoint_auth_signing_alg varchar(45) character set latin1 collate latin1_bin;
alter table client add default_max_age int(11);
alter table client add require_auth_time tinyint(1) not null default 0;
alter table client add initiate_login_uri varchar(250);

create table grant_type (
    id int(11) not null auto_increment,
    code varchar(45) character set latin1 collate latin1_bin not null,
    implementation varchar(20) character set latin1 collate latin1_bin not null, 
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table grant_type add constraint gt_code_uq_idx unique (code);
insert into grant_type(code, implementation) values('authorization_code', 'openid');
insert into grant_type(code, implementation) values('implicit', 'openid');
insert into grant_type(code, implementation) values('refresh_token', 'openid');
insert into grant_type(code, implementation) values('client_credentials', 'oauth');


create table client_grant_type (
    client_id int(11) not null,
    grant_type_id int(11) not null,
    primary key(client_id, grant_type_id)
);

alter table client_grant_type add constraint cgt_client_client_id_fk foreign key (client_id) references client(id) on delete cascade;
alter table client_grant_type add constraint cgt_grant_type_grant_id_fk foreign key (grant_type_id) references grant_type(id) on delete cascade;

create table client_contact (
    id int(11) not null auto_increment,
    email varchar(100) not null,
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table client_contact add constraint cc_client_id_fk foreign key (client_id) references client(id) on delete cascade;
alter table client_contact add constraint cc_client_email_uq_idx unique (email, client_id);

create table client_request_uris (
    id int(11) not null auto_increment,
    uri varchar(250) not null,
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id)
);

alter table client_request_uris add constraint crqu_client_id_fk foreign key (client_id) references client(id) on delete cascade;
alter table client_request_uris add constraint crqu_client_uri_uq_idx unique (uri, client_id);
