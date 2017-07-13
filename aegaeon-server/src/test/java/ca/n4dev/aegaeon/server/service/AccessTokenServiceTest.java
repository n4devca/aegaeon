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

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.server.model.AccessToken;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.model.User;

/**
 * AccessTokenServiceTest.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 30, 2017
 */
@Transactional
public class AccessTokenServiceTest extends BaseTokenServiceTest {

    @Autowired
    AccessTokenService accessTokenService;
    
    @Test
    public void testFindOne() {
        AccessToken token = accessTokenService.findById(1L);
        Assert.assertNull(token);
    }
    
    @Test
    public void testCreateOneForClient() {
        Client client = clientService.findByPublicId(CLIENT_AUTH);
        Assert.assertNotNull(client);
        
        List<Scope> scopes = scopeService.findScopeFromString("openid");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 1);
        
        AccessToken token = accessTokenService.createClientAccessToken(client, scopes);
        Assert.assertNotNull(token);
    }
    
    @Test
    public void testCreateForUser() {
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES);
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 2);
        
        Client client = clientService.findByPublicId(CLIENT_IMPL);
        Assert.assertNotNull(client);
        
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        
        AccessToken token = this.accessTokenService.createToken(user, client, scopes);
        Assert.assertNotNull(token);
        Assert.assertNotNull(token.getScopes());
        Assert.assertNotNull(token.getValidUntil().isAfter(LocalDateTime.now()));
    }
    
    
    @Test(expected = ServerException.class)
    public void testCreateForUserNotAllowed() {
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES);
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 2);
        
        Client client = clientService.findByPublicId(CLIENT_IMPL_UNALLOWED);
        Assert.assertNotNull(client);
        
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        
        AccessToken token = this.accessTokenService.createToken(user, client, scopes);
        Assert.assertNotNull(token);
        Assert.assertNotNull(token.getScopes());
        Assert.assertNotNull(token.getValidUntil().isAfter(LocalDateTime.now()));
    }
    
}
