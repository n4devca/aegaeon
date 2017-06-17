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
 
insert into users(username, email, name, uniqueIdentifier, passwd, enabled) 
values('admin@localhost', 'admin@localhost', 'Remi Guillemette', uuid(), '$2a$10$cyonNff/DPqlt8y.mFOowOkv3k3I5Iygrtqfw2jiUYTEEfhFPla2C', 1);
select last_insert_id() into @uid;

insert into user_authority(user_id, authority_id)
select @uid, id
from authority;


insert into client(grant_type, name, logourl, public_id, secret, provider_name)
values('AUTHORIZATIONCODE', 'ca.n4dev.auth.client', 'https://n4dev.ca/aegaeon/logo1.jpg', 'ca.n4dev.auth.client', 'kjaskas8993jnskajksj', 'RSA_RS512');
select last_insert_id() into @client_auth;

insert into client(grant_type, name, logourl, public_id, secret, provider_name)
values('AUTHORIZATIONCODE', 'ca.n4dev.auth.client2', 'https://n4dev.ca/aegaeon/logo1.jpg', 'ca.n4dev.auth.client2', 'kjaskas8993jnskajksj', 'RSA_RS512');
select last_insert_id() into @client_auth2;

insert into client(grant_type, name, logourl, public_id, secret, provider_name)
values('AUTHORIZATIONCODE', 'ca.n4dev.auth.client3', 'https://n4dev.ca/aegaeon/logo1.jpg', 'ca.n4dev.auth.client3', 'kjaskas8993jnskajksj', 'HMAC_HS512');
select last_insert_id() into @client_auth3;

insert into client(grant_type, name, logourl, public_id, secret, provider_name)
values('IMPLICIT', 'ca.n4dev.auth.client.impl', 'https://n4dev.ca/aegaeon/logo2.jpg', 'ca.n4dev.auth.client.impl', 'kjaskas8993jnskajksj', 'RSA_RS512');
select last_insert_id() into @client_impl;

insert into client_scope(client_id, scope_id)
select @client_impl, id
from scope where name in ('openid', 'profile');

insert into client_scope(client_id, scope_id)
select @client_auth, id
from scope where name in ('openid', 'profile', 'offline_access');

-- Code but no offline
insert into client_scope(client_id, scope_id)
select @client_auth2, id
from scope where name in ('openid', 'profile');

insert into client_scope(client_id, scope_id)
select @client_auth3, id
from scope where name in ('openid', 'profile');

-- Redirection
insert into client_redirection(client_id, url) values(@client_auth, 'http://localhost/login.html');
insert into client_redirection(client_id, url) values(@client_auth, 'http://app2.localhost/login.html');
insert into client_redirection(client_id, url) values(@client_auth2, 'http://localhost/login.html');
insert into client_redirection(client_id, url) values(@client_impl, 'http://localhost/login.html');
insert into client_redirection(client_id, url) values(@client_auth3, 'http://localhost/login.html');

insert into client(grant_type, name, logourl, public_id, secret, provider_name)
values('IMPLICIT', 'ca.n4dev.auth.client.impl.notallowed', 'https://n4dev.ca/aegaeon/logo2.jpg', 'ca.n4dev.auth.client.impl.notallowed', 'kjaskas8993jnskajksjsasas2323', 'RSA_RS512');
select last_insert_id() into @client_impl_unath;

insert into client_redirection(client_id, url) values(@client_impl_unath, 'http://bad.localhost/login.html');


-- Allow 2 clients
insert into users_authorization(user_id, client_id, scopes) values(@uid, @client_auth, 'openid profile offline_access');
-- This is wrong: client auth 2 did not request offline_access, the tokenservice should shield for it
insert into users_authorization(user_id, client_id, scopes) values(@uid, @client_auth2, 'openid profile offline_access');
-- OK
insert into users_authorization(user_id, client_id, scopes) values(@uid, @client_auth3, 'openid profile');
-- This is wrong: implicit client should not have offline_access, the tokenservice should shield for it
insert into users_authorization(user_id, client_id, scopes) values(@uid, @client_impl, 'openid profile offline_access');
-- but not the third one