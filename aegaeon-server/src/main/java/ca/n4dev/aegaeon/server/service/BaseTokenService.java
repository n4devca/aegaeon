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

import org.springframework.data.jpa.repository.JpaRepository;

import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.server.model.BaseEntity;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.model.UserAuthorization;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * BaseTokenService.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 1, 2017
 */
public abstract class BaseTokenService<T extends BaseEntity, J extends JpaRepository<T, Long>> extends BaseService<T, J> {

    protected UserService userService;
    protected ClientService clientService;
    protected UserAuthorizationService userAuthorizationService;
    protected TokenFactory tokenFactory;
    /**
     * @param pRepository
     */
    public BaseTokenService(J pRepository,
                            TokenFactory pTokenFactory,
                            UserService pUserService,
                            ClientService pClientService,
                            UserAuthorizationService pUserAuthorizationService) {
        super(pRepository);
        this.tokenFactory = pTokenFactory;
        this.userService = pUserService;
        this.clientService = pClientService;
        this.userAuthorizationService = pUserAuthorizationService;
    }
    
    protected void validate(User pUser, Client pClient, List<Scope> pScopes, TokenType pTokenType) {
        Assert.notNull(pUser, "A token cannot be created without a user");
        Assert.notNull(pClient, "A token cannot be created without a client");
        
        // Make sure the user has authorize this
        UserAuthorization authorization = this.userAuthorizationService.findByUserIdAndClientId(pUser.getId(), pClient.getId());
        Assert.notNull(authorization, "The user has not authorized this client.");

        // Make sure the scopes are authorized
        Assert.isTrue(isSameScope(Utils.explode(" ", authorization.getScopes(), s -> s), pScopes), 
                      "The authorized scopes and the requested scopes are different.");
        
        // If the type is refresh_token, check client scope
        if (pTokenType == TokenType.REFRESH_TOKEN) {
            
        }
    }
    
    protected boolean isSameScope(List<String> pAuthorizedScopes, List<Scope> pRequestedScopes) {
        
        boolean ok = false;
        
        for (Scope s : pRequestedScopes) {
            ok = false;
            
            for (String us : pAuthorizedScopes) {
                if (us.equalsIgnoreCase(s.getName())) {
                    ok = true;
                    break;
                }
            }
            
            // One scope is not authorized, return
            if (!ok) {
                return false;
            }
        }
        
        return true;
    }
}
