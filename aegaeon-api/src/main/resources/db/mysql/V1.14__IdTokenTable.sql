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
 
create table id_token (
    id int(11) not null auto_increment,
    token varchar(2000) character set latin1 collate latin1_bin not null,
    validuntil datetime not null,
    user_id int(11) default null,
    client_id int(11) not null,
    version int(11) not null default '0',
    createdat timestamp not null default current_timestamp,
    updatedat timestamp null default null on update current_timestamp,
    scopes varchar(200) collate utf8mb4_bin default null,
    primary key (id),
    key idtk_user_id_user_fk (user_id),
    key idtk_client_id_client_fk (client_id),
    constraint idtk_client_id_client_fk foreign key (client_id) references client (id) on delete cascade on update no action,
    constraint idtk_user_id_user_fk foreign key (user_id) references users (id) on delete cascade on update no action
) engine=innodb;

alter table authorization_code add noonce varchar(40);

