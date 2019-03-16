/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.service;

import java.util.Set;

import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * RefreshTokenServiceTest.java
 * <p>
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 2, 2017
 */
public class RefreshTokenServiceTest extends BaseTokenServiceTest {

    @Autowired
    RefreshTokenService refreshTokenService;

    private TokenRequest getTokenRequest(String pFlow, GrantType pGrantType, String pClientId, Set<ScopeView> pScopes) {
        TokenRequest tokenRequest = new TokenRequest(pGrantType.toString(),
                                                     "xx",
                                                     pClientId,
                                                     "",
                                                     Utils.join(pScopes, pScopeView -> pScopeView.getName()),
                                                     null);
        tokenRequest.setResponseType(pFlow);

        return tokenRequest;
    }

    @Test
    @WithMockUser(username = CLIENT_AUTH, roles = {"CLIENT"})
    public void testGetRefreshToken() {

        User user = getUser(USERNAME);
        Assert.assertNotNull(user);

        Set<ScopeView> scopes = scopeService.getValidScopes("openid profile offline_access");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 3);

        final TokenRequest tokenRequest = getTokenRequest(FlowUtils.RTYPE_AUTH_CODE, GrantType.AUTHORIZATION_CODE, CLIENT_AUTH, scopes);

        RefreshToken token = refreshTokenService.createToken(tokenRequest, user.getId(), CLIENT_AUTH, scopes);
        Assert.assertNotNull(token);
    }

    @Test
    @WithMockUser(username = CLIENT_AUTH, roles = {"CLIENT"})
    public void testErrorMissingOffline() {
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);

        Set<ScopeView> scopes = scopeService.getValidScopes("openid profile");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 2);

        final TokenRequest tokenRequest = getTokenRequest(FlowUtils.RTYPE_AUTH_CODE, GrantType.AUTHORIZATION_CODE, CLIENT_AUTH, scopes);

        RefreshToken token = refreshTokenService.createToken(tokenRequest, user.getId(), CLIENT_AUTH, scopes);
        // Creation is skipped, so should be null
        Assert.assertNull(token);
    }

    @WithMockUser(username = CLIENT_IMPL, roles = {"CLIENT"})
    public void testErrorImplicitClient() {
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);

        Set<ScopeView> scopes = scopeService.getValidScopes("openid profile offline_access");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 3);

        final TokenRequest tokenRequest = getTokenRequest(FlowUtils.RTYPE_IMPLICIT_FULL, GrantType.IMPLICIT, CLIENT_IMPL, scopes);

        RefreshToken token = refreshTokenService.createToken(tokenRequest, user.getId(), CLIENT_IMPL, scopes);

        Assert.assertNull(token);
    }

    @WithMockUser(username = CLIENT_AUTH_2, roles = {"CLIENT"})
    public void testErrorNotAuthorizedClient() {
        User user = getUser(USERNAME);
        Assert.assertNotNull(user);

        Set<ScopeView> scopes = scopeService.getValidScopes("openid profile offline_access");
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 3);

        final TokenRequest tokenRequest = getTokenRequest(FlowUtils.RTYPE_AUTH_CODE, GrantType.AUTHORIZATION_CODE, CLIENT_AUTH_2, scopes);

        RefreshToken token = refreshTokenService.createToken(tokenRequest, user.getId(), CLIENT_AUTH_2, scopes);

        Assert.assertNull(token);
    }
}
