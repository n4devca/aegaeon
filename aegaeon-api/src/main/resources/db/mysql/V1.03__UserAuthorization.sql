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
 
 create table users_authorization (
    id int(11) not null auto_increment,
    user_id int(11) not null,
    client_id int(11) not null,
    version int(11) not null default 0,
    createdat timestamp not null default CURRENT_TIMESTAMP,
    updatedat timestamp null on update CURRENT_TIMESTAMP,
    primary key(id),
    unique key uath_uid_cid_uq (user_id, client_id),
    constraint uath_userid_fk foreign key (user_id) references users (id) ON DELETE CASCADE ON UPDATE NO ACTION,
    constraint uath_clientid_fk foreign key (user_id) references users (id) ON DELETE CASCADE ON UPDATE NO ACTION
 );
 
 