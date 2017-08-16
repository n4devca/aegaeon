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
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.repository.AccessTokenRepository;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * AccessTokenService.java
 * 
 * Service managing the creation of access token.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
@Service
public class AccessTokenService extends BaseTokenService<AccessToken, AccessTokenRepository> {

    
    /**
     * Default Constructor.
     * @param pRepository
     * @param pTokenFactory
     * @param pUserService
     * @param pClientService
     * @param pUserAuthorizationService
     * @param pPayloadProvider
     */
    @Autowired
    public AccessTokenService(AccessTokenRepository pRepository, 
                              TokenFactory pTokenFactory, 
                              UserService pUserService,
                              ClientService pClientService,
                              UserAuthorizationService pUserAuthorizationService,
                              PayloadProvider pPayloadProvider) {
        
        super(pRepository, pTokenFactory, pUserService, pClientService, pUserAuthorizationService, pPayloadProvider);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#createManagedToken(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    AccessToken createManagedToken(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) throws Exception {
        
        Token token = this.tokenFactory.createToken(pUser, 
                                                    pClient, 
                                                    pClient.getAccessTokenSeconds(), 
                                                    ChronoUnit.SECONDS, 
                                                    Collections.emptyMap());
        
        AccessToken at = new AccessToken();
        at.setClient(pClient);
        at.setUser(pUser);
        
        at.setToken(token.getValue());
        at.setValidUntil(token.getValidUntil());
        
        if (pScopes != null) {
            at.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
        }
        
        return this.save(at);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#getManagedTokenType()
     */
    @Override
    TokenType getManagedTokenType() {
        return TokenType.ACCESS_TOKEN;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#validate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    void validate(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) throws Exception {
        // No more validations for access token.
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#isTokenToCreate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    boolean isTokenToCreate(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) {
        
        // OpenID: don't return access token if the requested type is only id_token
        if (pFlow.getResponseType().length == 1 && pFlow.getResponseType()[0].equals(FlowFactory.PARAM_ID_TOKEN)) {
            return false;
        }
        
        return true;
    }
    
    @Transactional
    public AccessToken createClientAccessToken(Client pClient, List<Scope> pScopes) {
        Assert.notNull(pClient, ServerExceptionCode.CLIENT_EMPTY);
        
        // Compare authorized and requested scopes
        Assert.isTrue(isAuthorized(pClient.getScopesAsNameList(), pScopes), 
                ServerExceptionCode.SCOPE_UNAUTHORIZED);
        
        try {
            Token token = this.tokenFactory.createToken(new ClientOAuthUser(pClient), pClient, 
                                                        pClient.getAccessTokenSeconds(), ChronoUnit.SECONDS,
                                                        Collections.emptyMap());
            
            AccessToken at = new AccessToken();
            at.setClient(pClient);
            //at.setUser(pUser);
            
            at.setToken(token.getValue());
            at.setValidUntil(token.getValidUntil());
            
            if (pScopes != null) {
                at.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
            }
            
            return this.save(at);
        } catch (Exception e) {
            // TODO(RG) : throw something meaningful
            throw new ServerException(e);
        }
    }
    
    
    public AccessToken findByTokenValue(String pTokenValue) {
        return this.getRepository().findByToken(pTokenValue);
    }
    
    
    private static final class ClientOAuthUser implements OAuthUser {

        private Client client;
        
        private ClientOAuthUser(Client pClient) {
            this.client = pClient;
        }
        
        /* (non-Javadoc)
         * @see ca.n4dev.aegaeon.api.token.OAuthUser#getId()
         */
        @Override
        public Long getId() {
            return this.client.getId();
        }

        /* (non-Javadoc)
         * @see ca.n4dev.aegaeon.api.token.OAuthUser#getUniqueIdentifier()
         */
        @Override
        public String getUniqueIdentifier() {
            // TODO Auto-generated method stub
            return this.client.getPublicId();
        }

        /* (non-Javadoc)
         * @see ca.n4dev.aegaeon.api.token.OAuthUser#getEmail()
         */
        @Override
        public String getEmail() {
            // TODO Auto-generated method stub
            return this.client.getPublicId();
        }

        /* (non-Javadoc)
         * @see ca.n4dev.aegaeon.api.token.OAuthUser#getName()
         */
        @Override
        public String getName() {
            return this.client.getName();
        }
        
    }


}
