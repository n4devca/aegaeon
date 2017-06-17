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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.server.model.RefreshToken;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.model.User;

/**
 * RefreshTokenServiceTest.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 2, 2017
 */
public class RefreshTokenServiceTest extends BaseTokenServiceTest {
    
    @Autowired
    RefreshTokenService refreshTokenService;
    
    
    @Test
    public void testGetRefreshToken() {
        
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        
        List<Scope> scopes = scopeService.findScopeFromString("openid profile offline_access");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 3);
        
        RefreshToken token = refreshTokenService.createRefreshToken(user.getId(), CLIENT_AUTH, scopes);
        Assert.assertNotNull(token);
    }
    
    @Test
    public void testErrorMissingOffline() {
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        
        List<Scope> scopes = scopeService.findScopeFromString("openid profile");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 2);
        
        RefreshToken token = refreshTokenService.createRefreshToken(user.getId(), CLIENT_AUTH, scopes);
        // Creation is skipped, so should be null
        Assert.assertNull(token);
    }
    
    @Test(expected = ServerException.class)
    public void testErrorImplicitClient() {
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        
        List<Scope> scopes = scopeService.findScopeFromString("openid profile offline_access");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 3);
        
        RefreshToken token = refreshTokenService.createRefreshToken(user.getId(), CLIENT_IMPL, scopes);
        
        // Exception throwed before getting here
        Assert.assertNull(token);
    }
    
    @Test(expected = ServerException.class)
    public void testErrorNotAuthorizedClient() {
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        
        List<Scope> scopes = scopeService.findScopeFromString("openid profile offline_access");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 3);
        
        RefreshToken token = refreshTokenService.createRefreshToken(user.getId(), CLIENT_AUTH_2, scopes);
        
        // Exception throwed before getting here
        Assert.assertNull(token);
    }
}
