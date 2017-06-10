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

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.RefreshToken;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.repository.RefreshTokenRepository;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * RefreshTokenService.java
 * 
 * Service managing RefreshToken.
 *
 * @author by rguillemette
 * @since Jun 1, 2017
 */
@Service
public class RefreshTokenService extends BaseTokenService<RefreshToken, RefreshTokenRepository> {

    private static final String REFRESH_TOKEN_ALG = "UUID";
    
    
    /**
     * Default Constructor.
     * @param pRepository RefreshToken repository.
     */
    @Autowired
    public RefreshTokenService(RefreshTokenRepository pRepository,
                                TokenFactory pTokenFactory, 
                                UserService pUserService,
                                ClientService pClientService,
                                UserAuthorizationService pUserAuthorizationService) {
        super(pRepository, pTokenFactory, pUserService, pClientService, pUserAuthorizationService);
    }
    
    RefreshToken createRefreshToken(Long pUserId, String pClientPublicId, List<Scope> pScopes) {
        
        if (contains(pScopes, OFFLINE_SCOPE)) {
            
            Assert.notNull(pUserId, ServerExceptionCode.USER_EMPTY);
            Assert.notEmpty(pClientPublicId, ServerExceptionCode.CLIENT_EMPTY);
            
            Client client = this.clientService.findByPublicId(pClientPublicId);
            User user = this.userService.findById(pUserId);
            
            return createRefreshToken(user, client, pScopes);
        }
        
        return null;
    }

    /**
     * 
     * @param pUser
     * @param pClient
     * @param pScopes
     * @return
     */
    RefreshToken createRefreshToken(User pUser, Client pClient, List<Scope> pScopes) {
        
        if (contains(pScopes, OFFLINE_SCOPE)) {
            
            validate(pUser, pClient, pScopes, TokenType.REFRESH_TOKEN);
        
            try {
                // A token is created only if requested.
                Token token = this.tokenFactory.createToken(pUser, pClient, REFRESH_TOKEN_ALG, pClient.getRefreshTokenSeconds(), ChronoUnit.SECONDS);
                
                
                RefreshToken rf = new RefreshToken();
                rf.setClient(pClient);
                rf.setUser(pUser);
                rf.setToken(token.getValue());
                rf.setValidUntil(token.getValidUntil());
                
                if (pScopes != null) {
                    
                    
                    
                    rf.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
                }
                
                return this.save(rf);
                    
                
            } catch (Exception e) {
                throw new ServerException(e);
            }
        
        }
        
        return null;
    }
    
    /**
     * Find a refresh token using its value and client to whom it has been granted.
     * @param pTokenValue The token value.
     * @param pClientId The client primary key.
     * @return A RefreshToken or null.
     */
    @Transactional(readOnly = true)
    public RefreshToken findByTokenValueAndClientId(String pTokenValue, Long pClientId) {
        return this.getRepository().findByTokenAndClientId(pTokenValue, pClientId);
    }
}
