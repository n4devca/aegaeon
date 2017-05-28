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

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.server.model.AccessToken;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.repository.AccessTokenRepository;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * AccessTokenService.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
@Service
public class AccessTokenService extends BaseService<AccessToken, AccessTokenRepository> {

    private TokenFactory tokenFactory;
    private UserService userService;
    private ClientService clientService;
    
    /**
     * @param pRepository
     */
    @Autowired
    public AccessTokenService(AccessTokenRepository pRepository, 
                              TokenFactory pTokenFactory, 
                              UserService pUserService,
                              ClientService pClientService) {
        
        super(pRepository);
        this.tokenFactory = pTokenFactory;
        this.userService = pUserService;
        this.clientService = pClientService;
    }

    @Transactional
    public AccessToken createAccessToken(Long pUserId, String pClientPublicId, List<Scope> pScopes) {
        Assert.notNull(pUserId, "This function need a user id");
        Assert.notEmpty(pClientPublicId, "This function need a client");
        
        Client client = this.clientService.findByPublicId(pClientPublicId);
        User user = this.userService.findById(pUserId);
        
        
        return createAccessToken(user, client, pScopes);
    }
    
    /**
     * Create an access token.
     * 
     * This function mainly create the "db entity". The actual token creation is delegate to {@link TokenFactory}.
     * 
     * @param pUser A user.
     * @param pClient A client.
     * @return The saved token.
     */
    @Transactional
    public AccessToken createAccessToken(User pUser, Client pClient, List<Scope> pScopes) {
        
        Assert.notNull(pUser, "An access token cannot be created without a user");
        Assert.notNull(pClient, "An access token cannot be created without a client");

        try {
            Token token = this.tokenFactory.createToken(pUser, pClient, pClient.getAccessTokenSeconds(), ChronoUnit.SECONDS);
            
            
            AccessToken at = new AccessToken();
            at.setClient(pClient);
            at.setUser(pUser);
            
            at.setToken(token.getValue());
            at.setValidUntil(token.getValidUntil());
            
            if (pScopes != null) {
                at.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
            }
            
            return this.save(at);
        } catch (Exception e) {
            // TODO(RG) : throw something meaningful
            throw new RuntimeException();
        }
    }
}
