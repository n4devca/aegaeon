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
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.repository.RefreshTokenRepository;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.mapper.TokenMapper;

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

    
    /**
     * Default Constructor.
     * @param pRepository RefreshToken repository.
     */
    @Autowired
    public RefreshTokenService(RefreshTokenRepository pRepository,
                                TokenFactory pTokenFactory, 
                                UserService pUserService,
                                ClientService pClientService,
                                UserAuthorizationService pUserAuthorizationService,
                                TokenMapper pTokenMapper) {
        super(pRepository, pTokenFactory, pUserService, pClientService, pUserAuthorizationService, pTokenMapper);
    }

    /**
     * Find a refresh token using its value and client to whom it has been granted.
     * @param pTokenValue The token value.
     * @param pClientId The client primary key.
     * @return A RefreshToken or null.
     */
    RefreshToken findByTokenValueAndClientId(String pTokenValue, Long pClientId) {
        return this.getRepository().findByTokenAndClientId(pTokenValue, pClientId);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#createManagedToken(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    RefreshToken createManagedToken(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) throws Exception {

        Token token = this.tokenFactory.createToken(pUser, pClient, 
                                                    TokenProviderType.UUID, 
                                                    pClient.getRefreshTokenSeconds(), ChronoUnit.SECONDS,
                                                    Collections.emptyMap());
        
        RefreshToken rf = new RefreshToken();
        rf.setClient(pClient);
        rf.setUser(pUser);
        rf.setToken(token.getValue());
        rf.setValidUntil(token.getValidUntil());
        
        if (pScopes != null) {
            rf.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
        }
        
        return this.save(rf);
        
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#validate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    void validate(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) throws Exception {
        
        if (!this.clientService.hasScope(pClient.getId(), OFFLINE_SCOPE) 
                || !this.clientService.hasGrantType(pClient.getId(), GrantType.CODE_AUTH_CODE)) {
            throw new ServerException(ServerExceptionCode.SCOPE_UNAUTHORIZED_OFFLINE);            
        }
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#getManagedTokenType()
     */
    @Override
    TokenType getManagedTokenType() {
        return TokenType.REFRESH_TOKEN;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#isTokenToCreate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    boolean isTokenToCreate(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) {
        
        if (Utils.contains(pFlow.getResponseType(), FlowFactory.PARAM_CODE)
                && this.clientService.hasScope(pClient.getId(), OFFLINE_SCOPE) 
                && this.clientService.hasGrantType(pClient.getId(), GrantType.CODE_AUTH_CODE)) {
            
            return true;            
        }
        
        return false;
    }
}
