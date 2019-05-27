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

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.InvalidAuthorizationCodeException;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.api.repository.RefreshTokenRepository;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import ca.n4dev.aegaeon.server.view.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#createManagedToken(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon
     * .server.model.Client, java.util.List)
     */
    @Override
    RefreshToken createManagedToken(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) throws Exception {

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
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#validate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server.model
     * .Client, java.util.List)
     */
    @Override
    void validate(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) throws Exception {

        if (!this.clientService.hasScope(pClient.getId(), OFFLINE_SCOPE)
                || !this.clientService.hasFlow(pClient.getId(), Flow.authorization_code)) {
            throw new InvalidAuthorizationCodeException(pTokenRequest,
                                                        "Cannot create a refresh token for this client or flow.");
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
     * @see ca.n4dev.aegaeon.server.service.BaseTokenService#isTokenToCreate(ca.n4dev.aegaeon.server.model.User, ca.n4dev.aegaeon.server
     * .model.Client, java.util.List)
     */
    @Override
    boolean isTokenToCreate(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) {
        // No need to do all these check if the offline_access has not been requested.
        return Utils.isOneTrue(pScopes, pScope -> OFFLINE_SCOPE.equals(pScope.getName()));
    }
}
