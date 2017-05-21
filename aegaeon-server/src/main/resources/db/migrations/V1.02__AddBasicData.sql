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

insert into client_type(code) values('AUTH_CODE');
insert into client_type(code) values('IMPLICIT');
insert into client_type(code) values('CLIENT_CREDENTIALS');

insert into scope(name, description, issystem) values('openid', 'openid scope', 1);
insert into scope(name, description, issystem) values('profile', 'user profile', 1);

insert into authority(code) values('ROLE_USER');
insert into authority(code) values('ROLE_ADMIN');
