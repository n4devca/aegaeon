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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.repository.IdTokenRepository;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.api.token.payload.Claims;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.mapper.TokenMapper;

/**
 * IdTokenService.java
 * 
 * Service managing the creation of id token.
 *
 * @author by rguillemette
 * @since Jul 5, 2017
 */
@Service
public class IdTokenService extends BaseTokenService<IdToken, IdTokenRepository> {

    /**
     * @param pRepository
     * @param pTokenFactory
     * @param pUserService
     * @param pClientService
     * @param pUserAuthorizationService
     */
    @Autowired
    public IdTokenService(IdTokenRepository pRepository, 
                          TokenFactory pTokenFactory, 
                          UserService pUserService, 
                          ClientService pClientService,
                          UserAuthorizationService pUserAuthorizationService,
                          PayloadProvider pPayloadProvider,
                          TokenMapper pTokenMapper) {
        super(pRepository, pTokenFactory, pUserService, pClientService, pUserAuthorizationService, pPayloadProvider, pTokenMapper);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#createManagedToken(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    IdToken createManagedToken(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) throws Exception {

        // Create Payload.
        List<String> scopes = Utils.convert(pScopes, s -> s.getName());
        Map<String, String> payload = this.payloadProvider.createPayload(pUser, pClient, scopes);
        
        // If we need to include nonce param to prevent replay attack
        if (Utils.isNotEmpty(pFlow.getNonce())) {
            payload.put(Claims.NONCE, pFlow.getNonce());
        }

        // Create Token.
        Token token = this.tokenFactory.createToken(pUser, pClient, 
                                                    TokenProviderType.RSA_RS512, 
                                                    pClient.getIdTokenSeconds(), ChronoUnit.SECONDS,
                                                    payload);
        
        IdToken idt = new IdToken();
        idt.setClient(pClient);
        idt.setUser(pUser);
        
        idt.setToken(token.getValue());
        idt.setValidUntil(token.getValidUntil());
        
        if (pScopes != null) {
            idt.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
        }
        
        return this.save(idt);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#validate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    void validate(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) throws Exception {
        // Must have the right scope
        if (!contains(pScopes, FlowFactory.PARAM_ID_TOKEN)) {
            throw new ServerException(ServerExceptionCode.SCOPE_INVALID);
        }
        
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#getManagedTokenType()
     */
    @Override
    TokenType getManagedTokenType() {
        return TokenType.ID_TOKEN;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#isTokenToCreate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model.Client, java.util.List)
     */
    @Override
    boolean isTokenToCreate(Flow pFlow, User pUser, Client pClient, List<Scope> pScopes) {
        return contains(pScopes, FlowFactory.PARAM_ID_TOKEN);
    }

}
