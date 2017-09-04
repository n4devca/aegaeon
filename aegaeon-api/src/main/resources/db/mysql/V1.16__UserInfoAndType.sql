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

drop table if exists user_info_type;
create table user_info_type (
    id int(11) not null auto_increment,
    code varchar(40) not null,
    parent_id int(11),
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id),
    key usr_ift_parentid_idx (parent_id),
    unique key usr_ift_code_uq (code),
    constraint usr_ift_parentid_fk foreign key (parent_id) references user_info_type (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- Parent
insert into user_info_type(code) values('PHONE');
insert into user_info_type(code) values('SOCIALMEDIA');
insert into user_info_type(code) values('ADDRESS');
insert into user_info_type(code) values('PERSONAL');


-- Some types
select id into @phoneid from user_info_type where code = 'PHONE';
insert into user_info_type(code, parent_id) values('HOME_PHONE', @phoneid);
insert into user_info_type(code, parent_id) values('WORK_PHONE', @phoneid);
insert into user_info_type(code, parent_id) values('MOBILE_PHONE', @phoneid);
insert into user_info_type(code, parent_id) values('OTHER_PHONE', @phoneid);

select id into @socialid from user_info_type where code = 'SOCIALMEDIA';
insert into user_info_type(code, parent_id) values('FACEBOOK_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('LINKEDIN_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('WHATSAPP_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('WECHAT_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('SNAPCHAT_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('TWITTER_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('INSTAGRAM_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('PINTEREST_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('GOOGLEPLUS_SOCIALMEDIA', @socialid);
insert into user_info_type(code, parent_id) values('OTHER_SOCIALMEDIA', @socialid);

select id into @addrid from user_info_type where code = 'ADDRESS';
insert into user_info_type(code, parent_id) values('HOME_ADDRESS', @addid);
insert into user_info_type(code, parent_id) values('WORK_ADDRESS', @addid);
insert into user_info_type(code, parent_id) values('OTHER_ADDRESS', @addid);

select id into @persid from user_info_type where code = 'PERSONAL';
insert into user_info_type(code, parent_id) values('GENDER_PERSONAL', @persid);
insert into user_info_type(code, parent_id) values('BIRTHDAY_PERSONAL', @persid);
insert into user_info_type(code, parent_id) values('MARITALSTATUS_PERSONAL', @persid);
insert into user_info_type(code, parent_id) values('OTHER_PERSONAL', @persid);

drop table if exists user_info;
create table user_info (
    id int(11) not null auto_increment,
    user_id int(11) not null,
    user_info_type_id int(11) not null,
    other_name varchar(40),
    value varchar(1000) not null,
    note varchar(1000),
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id),
    key usr_if_userid_idx (user_id),
    key usr_if_userinftypeid_idx (user_info_type_id),
    constraint usr_if_userid_fk foreign key (user_id) references users (id) ON DELETE CASCADE ON UPDATE NO ACTION,
    constraint usr_if_userinftypeid_fk foreign key (user_info_type_id) references user_info_type (id) ON DELETE CASCADE ON UPDATE NO ACTION
);