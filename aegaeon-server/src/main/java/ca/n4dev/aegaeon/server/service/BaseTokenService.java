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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.model.BaseEntity;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserAuthorization;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.api.exception.InvalidClientIdException;
import ca.n4dev.aegaeon.api.exception.MissingUserInformationException;
import ca.n4dev.aegaeon.api.exception.UnauthorizedClient;
import ca.n4dev.aegaeon.api.exception.UnauthorizedScope;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import ca.n4dev.aegaeon.server.view.mapper.TokenMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * BaseTokenService.java
 *
 * The base class of all JPA token service.
 *
 * @author by rguillemette
 * @since Jun 1, 2017
 */
public abstract class BaseTokenService<T extends BaseEntity, J extends JpaRepository<T, Long>> extends BaseSecuredService<T, J> {


    public static final String OFFLINE_SCOPE = "offline_access";

    protected UserService userService;
    protected ClientService clientService;
    protected UserAuthorizationService userAuthorizationService;
    protected TokenFactory tokenFactory;
    protected TokenMapper tokenMapper;

    /**
     * @param pRepository
     */
    public BaseTokenService(J pRepository,
                            TokenFactory pTokenFactory,
                            UserService pUserService,
                            ClientService pClientService,
                            UserAuthorizationService pUserAuthorizationService,
                            TokenMapper pTokenMapper) {
        super(pRepository);
        this.tokenFactory = pTokenFactory;
        this.userService = pUserService;
        this.clientService = pClientService;
        this.userAuthorizationService = pUserAuthorizationService;
        this.tokenMapper = pTokenMapper;
    }


    abstract T createManagedToken(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) throws Exception;

    abstract boolean isTokenToCreate(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes);

    abstract void validate(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) throws Exception;

    abstract TokenType getManagedTokenType();

    @Transactional
    T createToken(TokenRequest pTokenRequest, Long pUserId, String pClientPublicId, Set<ScopeView> pScopes) throws ServerException {

        Assert.notNull(pUserId, () -> new MissingUserInformationException(pTokenRequest));
        Assert.notEmpty(pClientPublicId, () -> new InvalidClientIdException(pTokenRequest));

        Client client = this.clientService.findByPublicId(pClientPublicId);
        User user = this.userService.findById(pUserId);

        return createToken(pTokenRequest, user, client, pScopes);
    }

    /**
     * Create a Token.
     * @param pUser
     * @param pClient
     * @param pScopes
     * @return
     */
    T createToken(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) throws ServerException {

        try {
            // Basic Validation (not null, authorize)
            basicValidation(pTokenRequest, pUser, pClient, pScopes);

            if (isTokenToCreate(pTokenRequest, pUser, pClient, pScopes)) {
                // Call Service Validation
                validate(pTokenRequest, pUser, pClient, pScopes);

                return createManagedToken(pTokenRequest, pUser, pClient, pScopes);

            }

            return null;
        } catch (ServerException se) {
            // rethrow
            throw se;
        } catch (Exception e) {
            // wrap
            throw new ServerException(e);
        }

    }

    protected void basicValidation(TokenRequest pTokenRequest, User pUser, Client pClient, Set<ScopeView> pScopes) {
        Assert.notNull(pUser, () -> new MissingUserInformationException(pTokenRequest));
        Assert.notNull(pClient, () -> new InvalidClientIdException(pTokenRequest));

        // Make sure the user has authorize this
        UserAuthorization authorization = this.userAuthorizationService.findByUserIdAndClientId(pUser.getId(), pClient.getId());
        Assert.notNull(authorization, () -> new UnauthorizedClient(pTokenRequest));

        // Make sure the scopes are authorized
        validateAuthorizedScope(pTokenRequest, Utils.explode(authorization.getScopes(), s -> s), pScopes);
    }

    protected void validateAuthorizedScope(TokenRequest pTokenRequest, List<String> pAuthorizedScopes, Set<ScopeView> pRequestedScopes) {
        Map<String, Boolean> validatedScopes = new LinkedHashMap<>();

        for (ScopeView s : pRequestedScopes) {
            boolean ok = false;

            for (String us : pAuthorizedScopes) {
                if (us.equalsIgnoreCase(s.getName())) {
                    validatedScopes.put(s.getName(), true);
                    ok = true;
                    break;
                }
            }

            if (!ok) {
                validatedScopes.put(s.getName(), false);
            }
        }

        final boolean allTrue = validatedScopes.entrySet().stream().allMatch(pEntry -> pEntry.getValue());

        if (!allTrue) {

            final Set<String> unauthorizedScopes = validatedScopes.entrySet().stream()
                                                                  .filter(pEntry -> pEntry.getValue())
                                                                  .map(pEntry -> pEntry.getKey())
                                                                  .collect(Collectors.toSet());

            throw new UnauthorizedScope(pTokenRequest, unauthorizedScopes);
        }
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
