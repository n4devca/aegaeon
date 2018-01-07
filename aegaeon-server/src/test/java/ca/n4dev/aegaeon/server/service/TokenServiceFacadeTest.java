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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.TokenResponse;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * TokenServiceFacadeTest.java
 * 
 * Test TokenServiceFacade with different configuration.
 *
 * @author by rguillemette
 * @since Jun 5, 2017
 */
public class TokenServiceFacadeTest extends BaseTokenServiceTest {

    private Authentication getFakeAuthentication() {
        Authentication a = new UsernamePasswordAuthenticationToken(getUser(USERNAME), "allo1122");
        
        return a;
    }
    
    @Autowired
    private TokenServicesFacade tokenServicesFacade;
    
    @Test
    @WithMockUser(username = CLIENT_IMPL, roles = {"CLIENT"})
    public void accessTokenRSA() {
        
        Client client = clientService.findByPublicId(CLIENT_IMPL);
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES);
        List<ClientRedirection> redirections = clientService.findRedirectionsByclientId(client.getId());
        
        TokenResponse token = this.tokenServicesFacade.createTokenResponse(FlowFactory.implicit(), 
                                                                           client.getPublicId(), 
                                                                           Utils.join(scopes, s -> s.getName()), 
                                                                           redirections.get(0).getUrl(),
                                                                           getFakeAuthentication());
        
        Assert.assertNotNull(token);
        Assert.assertTrue(token.getTokenType().equals(TokenResponse.BEARER));
        Assert.assertNotNull(token.getAccessToken());
        Assert.assertNull(token.getRefreshToken());
        Assert.assertNotNull(token.getExpiresIn());
        Assert.assertTrue(Long.valueOf(token.getExpiresIn()) > 0);
    }
    
    @Test
    @WithMockUser(username = CLIENT_AUTH, roles = {"CLIENT"})
    public void accessTokenWithRefreshRSA() {
        Client client = clientService.findByPublicId(CLIENT_AUTH);
        User user = getUser(USERNAME);
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES + " offline_access");
        List<ClientRedirection> redirections = clientService.findRedirectionsByclientId(client.getId());
        
        TokenResponse token = this.tokenServicesFacade.createTokenResponse(FlowFactory.authCode(), 
                client.getPublicId(), 
                Utils.join(scopes, s -> s.getName()), 
                redirections.get(0).getUrl(), getFakeAuthentication());

        Assert.assertNotNull(token);
        Assert.assertTrue(token.getTokenType().equals(TokenResponse.BEARER));
        Assert.assertNotNull(token.getAccessToken());
        Assert.assertNotNull(token.getRefreshToken());
        Assert.assertNotNull(token.getExpiresIn());
        Assert.assertTrue(Long.valueOf(token.getExpiresIn()) > 0);
    }
    
    @Test
    @WithMockUser(username = CLIENT_AUTH_3, roles = {"CLIENT"})
    public void accessTokenHMAC() {
        Client client = clientService.findByPublicId(CLIENT_AUTH_3);
        User user = getUser(USERNAME);
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES);
        List<ClientRedirection> redirections = clientService.findRedirectionsByclientId(client.getId());
        
        TokenResponse token = this.tokenServicesFacade.createTokenResponse(FlowFactory.authCode(), 
                                                                           client.getPublicId(), 
                                                                           Utils.join(scopes, s -> s.getName()), 
                                                                           redirections.get(0).getUrl(),
                                                                           getFakeAuthentication());
        
        Assert.assertNotNull(token);
        Assert.assertTrue(token.getTokenType().equals(TokenResponse.BEARER));
        Assert.assertNotNull(token.getAccessToken());
        Assert.assertNull(token.getRefreshToken());
        Assert.assertNotNull(token.getExpiresIn());
        Assert.assertTrue(Long.valueOf(token.getExpiresIn()) > 0);
    }
    
    @Test(expected = ServerException.class)
    public void failNoRefreshScope() {
        Client client = clientService.findByPublicId(CLIENT_AUTH_2);
        User user = getUser(USERNAME);
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES + " offline_access");
        List<ClientRedirection> redirections = clientService.findRedirectionsByclientId(client.getId());
        
        this.tokenServicesFacade.createTokenResponse(FlowFactory.authCode(), 
                client.getPublicId(), 
                Utils.join(scopes, s -> s.getName()), 
                redirections.get(0).getUrl(), getFakeAuthentication());
        
        // Should not be here
        Assert.fail();
    }
    
    @Test(expected = ServerException.class)
    public void failImplicitAndRefreshScope() {
        Client client = clientService.findByPublicId(CLIENT_IMPL);
        User user = getUser(USERNAME);
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES + " offline_access");
        List<ClientRedirection> redirections = clientService.findRedirectionsByclientId(client.getId());
        
        this.tokenServicesFacade.createTokenResponse(FlowFactory.implicit(), 
                client.getPublicId(), 
                Utils.join(scopes, s -> s.getName()), 
                redirections.get(0).getUrl(), 
                getFakeAuthentication());

        // Should not be here
        Assert.fail();
    }

    @Test(expected = ServerException.class)
    public void failUnauthorizedClient() {
        Client client = clientService.findByPublicId(CLIENT_IMPL_UNALLOWED);
        User user = getUser(USERNAME);
        List<Scope> scopes = scopeService.findScopeFromString(SCOPES);
        List<ClientRedirection> redirections = clientService.findRedirectionsByclientId(client.getId());
        
        this.tokenServicesFacade.createTokenResponse(FlowFactory.authCode(), 
                client.getPublicId(), 
                Utils.join(scopes, s -> s.getName()), 
                redirections.get(0).getUrl(),
                getFakeAuthentication());

        // Should not be here
        Assert.fail();
    }
}
