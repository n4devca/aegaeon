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

select id into @userid from users where username = 'admin@localhost';
select id into @clientid from client where name = 'ca.n4dev.auth.client';

insert into access_token(token, client_id, user_id, scopes, validuntil)
values('eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjYS5uNGRldi5hdXRoLmNsaWVudCIsImF1ZCI6ImNhLm40ZGV2LmF1dGguY2xpZW50IiwiaXNzIjoibG9jYWxob3N0IiwiZXhwIjoxNTI1Mzc2MDUzfQ.P9ftJayHRmXJpABgTbOEoK4lVJzqTTwGdYI_wXbYKVUw_lpOb7mQdJ_k783jLdpmfG-9QsQ4BkcA-DZttMeSjdo8HmHLselO4Sj9H01L_9lSSRM14gMGpT8EQXrIgtdjmLOkqbq5L42Its0BOUx1qVu2lDQjqoNNuqrQ16ehp5wyQV9HoR7LlQ4EhU6jQoj1nBPROJQ_HQKIYkaWty8Uifab7ZKv4RQgSQESQDzvN1wPz88NiQKrgxO3ttFeZeGCLf5CdlEVXwv98ayoD4h933mcAb5fDElLd13xKa545HRCXvtHmlk1qWxC1S0Bj0blUoOtiND2QJDQud00f6bK0Q',
       @clientid,
       @userid,
       'openid profile',
       '2017-06-10');

insert into access_token(token, client_id, user_id, scopes, validuntil)
values('eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjYS5uNGRldi5hdXRoLmNsaWVudCIsImF1ZCI6ImNhLm40ZGV2LmF1dGguY2xpZW50IiwiaXNzIjoibG9jYWxob3N0IiwiZXhwIjoxODQwNzM1ODQwfQ.oHQLca1tq8HNwD51-BEBHyBBdYsaDR3iwoROesf5zMnsS5u07FRim_eRlH0Cqz9cXIggrXcBnTUxgVEYEB3ejCkVp8_wIuyhAPw1fsMDL60hxj1BdMi6SN43CWZ0CKR8wntaZeL1LGowHZiXAuPH5OENM-8SUPWJ_LoDXGbjUFaxf3ZXrraIIYbD-WyhH-MzoBNzXzAHewBdHBD41dJqJE-EhPD091rAz40h6aNdSC4zE021bdmASyqExLuFNxHGKdEGqzuLAFDkZn3yfzXNode3dv1RuZblfBeDBNzPuSra4IRDXb2OgGJ8qjXxqNj0h2kDEq_YolWvH8Revgl4JA',
       @clientid,
       @userid,
       'openid profile',
       '2027-06-10');

