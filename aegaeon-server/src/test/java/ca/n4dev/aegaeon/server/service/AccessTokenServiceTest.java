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

import java.time.LocalDateTime;
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * AccessTokenServiceTest.java
 *
 * AccessTokenService integrated tests.
 *
 * @author by rguillemette
 * @since May 30, 2017
 */
@Transactional
public class AccessTokenServiceTest extends BaseTokenServiceTest {

    @Autowired
    AccessTokenService accessTokenService;

    @Test
    public void testCreateForUser() {
        Set<ScopeView> scopes = scopeService.getValidScopes(SCOPES);
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 2);

        Client client = clientService.findByPublicId(CLIENT_IMPL);
        Assert.assertNotNull(client);

        User user = getUser(USERNAME);
        Assert.assertNotNull(user);
        AuthRequest authRequest = new AuthRequest();
        authRequest.setResponseType(FlowUtils.RTYPE_AUTH_CODE);

        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE.toString(),
                                                     "xx",
                                                     CLIENT_IMPL,
                                                     "",
                                                     Utils.join(scopes, pScopeView -> pScopeView.getName()),
                                                     null);
        tokenRequest.setResponseType(FlowUtils.RTYPE_AUTH_CODE);

        AccessToken token = this.accessTokenService.createToken(tokenRequest, user, client, scopes);
        Assert.assertNotNull(token);
        Assert.assertNotNull(token.getScopes());
        Assert.assertNotNull(token.getValidUntil().isAfter(LocalDateTime.now()));
    }


    @Test(expected = ServerException.class)
    public void testCreateForUserNotAllowed() {
        Set<ScopeView> scopes = scopeService.getValidScopes(SCOPES);
        Assert.assertNotNull(scopes);
        Assert.assertTrue(scopes.size() == 2);

        Client client = clientService.findByPublicId(CLIENT_IMPL_UNALLOWED);
        Assert.assertNotNull(client);

        User user = getUser(USERNAME);
        Assert.assertNotNull(user);

        TokenRequest tokenRequest = new TokenRequest(GrantType.IMPLICIT.toString(),
                                                     "xx",
                                                     CLIENT_IMPL_UNALLOWED,
                                                     "",
                                                     Utils.join(scopes, pScopeView -> pScopeView.getName()),
                                                     null);
        tokenRequest.setResponseType(FlowUtils.RTYPE_IMPLICIT_FULL);


        AccessToken token = this.accessTokenService.createToken(tokenRequest, user, client, scopes);
        Assert.assertNotNull(token);
        Assert.assertNotNull(token.getScopes());
        Assert.assertNotNull(token.getValidUntil().isAfter(LocalDateTime.now()));
    }


}
