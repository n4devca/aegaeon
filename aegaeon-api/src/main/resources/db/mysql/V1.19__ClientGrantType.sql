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
 
-- Drop foreign and primary
alter table client_grant_type drop foreign key cgt_client_client_id_fk;
alter table client_grant_type drop foreign key cgt_grant_type_grant_id_fk;
alter table client_grant_type drop primary key;

-- New primary key
alter table client_grant_type add id int(11) not null auto_increment PRIMARY KEY;

-- Re-add foreigns
alter table client_grant_type add constraint cgt_client_client_id_fk foreign key (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE;
alter table client_grant_type add constraint cgt_grant_type_grant_id_fk foreign key (`grant_type_id`) REFERENCES `grant_type` (`id`) ON DELETE CASCADE;
