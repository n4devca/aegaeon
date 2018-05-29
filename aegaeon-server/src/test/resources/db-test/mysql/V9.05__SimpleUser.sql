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


insert into users(username, name, uniqueIdentifier, passwd, enabled)
values('user@localhost', 'Remi Guillemette', uuid(), '$2a$10$5I2.hpEdy7NYvjS/GdWAKujreLRCLxHY/kFmtItf3SZjToRDvpUzy', 1);
select last_insert_id() into @uid;

insert into user_authority(user_id, authority_id)
select @uid, id
from authority
where code = 'ROLE_USER';

-- Get ca.n4dev.auth.client
select id into @clientid from client where public_id = 'ca.n4dev.auth.client';

-- Allow ca.n4dev.auth.client to do implicit
insert into client_auth_flow(client_id, flow) values(@clientid, 'IMPLICIT');

-- user@localhost authorized ca.n4dev.auth.client
insert into users_authorization(user_id, client_id, scopes) values(@uid, @clientid, 'openid profile offline_access');