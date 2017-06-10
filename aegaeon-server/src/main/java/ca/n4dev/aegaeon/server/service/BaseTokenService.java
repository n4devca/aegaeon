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

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.protocol.AuthorizationGrant;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.server.model.BaseEntity;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.ClientScope;
import ca.n4dev.aegaeon.server.model.ClientType;
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


    public static final String OFFLINE_SCOPE = "offline_access";
    
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
        Assert.notNull(pUser, ServerExceptionCode.USER_EMPTY);
        Assert.notNull(pClient, ServerExceptionCode.CLIENT_EMPTY);
        
        // Make sure the user has authorize this
        UserAuthorization authorization = this.userAuthorizationService.findByUserIdAndClientId(pUser.getId(), pClient.getId());
        Assert.notNull(authorization, ServerExceptionCode.CLIENT_UNAUTHORIZED);
        
        // Make sure the scopes are authorized
        Assert.isTrue(isAuthorized(Utils.explode(authorization.getScopes(), s -> s), pScopes), 
                      ServerExceptionCode.SCOPE_UNAUTHORIZED);
        
        // If the type is refresh_token, check client scope and the client type
        // TODO(RG): Only auth code or client cred is ok ?
        if (pTokenType == TokenType.REFRESH_TOKEN) {
            
            if (!hasClientScope(pClient, OFFLINE_SCOPE) 
                    || pClient.getGrantType() != AuthorizationGrant.AUTHORIZATIONCODE) {
                throw new ServerException(ServerExceptionCode.SCOPE_UNAUTHORIZED_OFFLINE);
            }
        }
    }
    
    protected boolean hasClientScope(Client pClient, String pScopeName) {
        if (pClient != null && Utils.isNotEmpty(pScopeName)) {
            for (ClientScope sc : pClient.getScopes()) {
                if (sc.getScope().getName().equals(pScopeName)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    protected boolean isAuthorized(List<String> pAuthorizedScopes, List<Scope> pRequestedScopes) {
        
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
    
    protected boolean contains(List<Scope> pScopes, String pScopeToCheck) {
        
        if (pScopes != null && pScopeToCheck != null && !pScopeToCheck.isEmpty()) {
            for (Scope s : pScopes) {
                if (s.getName().equals(pScopeToCheck)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
