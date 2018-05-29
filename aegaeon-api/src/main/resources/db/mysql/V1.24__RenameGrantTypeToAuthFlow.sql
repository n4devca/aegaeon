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

-- Rename and add column
rename table client_grant_type to client_auth_flow;
alter table client_auth_flow add flow varchar(45) character set latin1 collate latin1_bin;

-- Fix data
delete from client_auth_flow where selected = 0;
update client_auth_flow
    inner join grant_type on (client_auth_flow.grant_type_id = grant_type.id)
set client_auth_flow.flow = grant_type.code;

-- Drop old column and constrainst
Alter table client_auth_flow   
  drop index cgt_grant_type_grant_id_fk,
  drop foreign key cgt_grant_type_grant_id_fk;

alter table client_auth_flow drop column grant_type_id;

-- Add a unique
alter table client_auth_flow add unique index client_auth_flow_uq(client_id, flow);

delete from client_scope where selected = 0;
alter table client_scope drop column selected;
