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

alter table client_grant_type add selected tinyint(1) not null default 0;
alter table client_grant_type add version int(11) not null default 0;
alter table client_grant_type add createdat timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table client_grant_type add updatedat timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP;

-- set all true / 1
update client_grant_type set selected = 1;

-- Add missing
insert into client_grant_type(client_id, grant_type_id)
select c.client_id, c.grant_type_id
from (
select c.id as client_id, g.id as grant_type_id
from client c
    join grant_type g
) c 
    left outer join client_grant_type cg on (cg.client_id = c.client_id and cg.grant_type_id = c.grant_type_id)
where cg.client_id is null
;