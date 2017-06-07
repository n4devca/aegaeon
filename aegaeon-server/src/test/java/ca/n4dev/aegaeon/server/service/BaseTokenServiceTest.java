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
package ca.n4dev.aegaeon.server.service;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * BaseTokenServiceTest.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 3, 2017
 */
public abstract class BaseTokenServiceTest extends BaseServiceTest {
    
    protected static final String USERNAME = "admin@localhost";
    protected static final String SCOPES = "openid profile";
    protected static final String CLIENT_AUTH = "ca.n4dev.auth.client";
    protected static final String CLIENT_AUTH_2 = "ca.n4dev.auth.client2";
    protected static final String CLIENT_AUTH_3 = "ca.n4dev.auth.client3";
    protected static final String CLIENT_IMPL = "ca.n4dev.auth.client.impl";
    protected static final String CLIENT_IMPL_UNALLOWED = "ca.n4dev.auth.client.impl.notallowed";
    
    @Autowired
    protected ClientService clientService;
    
    @Autowired
    protected ScopeService scopeService;
    
    @Autowired
    protected UserService userService;
    
    
}
