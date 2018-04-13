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

import java.util.List;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.*;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.token.TokenType;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
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


    abstract T createManagedToken(AuthRequest pAuthRequest, User pUser, Client pClient, List<Scope> pScopes) throws Exception;

    abstract boolean isTokenToCreate(AuthRequest pAuthRequest, User pUser, Client pClient, List<Scope> pScopes);

    abstract void validate(AuthRequest pAuthRequest, User pUser, Client pClient, List<Scope> pScopes) throws Exception;

    abstract TokenType getManagedTokenType();

    @Transactional
    T createToken(AuthRequest pAuthRequest, Long pUserId, String pClientPublicId, List<Scope> pScopes) throws ServerException {

        Assert.notNull(pUserId, ServerExceptionCode.USER_EMPTY);
        Assert.notEmpty(pClientPublicId, ServerExceptionCode.CLIENT_EMPTY);

        Client client = this.clientService.findByPublicId(pClientPublicId);
        User user = this.userService.findById(pUserId);

        return createToken(pAuthRequest, user, client, pScopes);
    }

    /**
     * Create a Token.
     * @param pUser
     * @param pClient
     * @param pScopes
     * @return
     */
    T createToken(AuthRequest pAuthRequest, User pUser, Client pClient, List<Scope> pScopes) throws ServerException {

        try {
            // Basic Validation (not null, authorize)
            basicValidation(pUser, pClient, pScopes);

            if (isTokenToCreate(pAuthRequest, pUser, pClient, pScopes)) {
                // Call Service Validation
                validate(pAuthRequest, pUser, pClient, pScopes);

                return createManagedToken(pAuthRequest, pUser, pClient, pScopes);

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

    protected void basicValidation(User pUser, Client pClient, List<Scope> pScopes) {
        Assert.notNull(pUser, ServerExceptionCode.USER_EMPTY);
        Assert.notNull(pClient, ServerExceptionCode.CLIENT_EMPTY);

        // Make sure the user has authorize this
        UserAuthorization authorization = this.userAuthorizationService.findByUserIdAndClientId(pUser.getId(), pClient.getId());
        Assert.notNull(authorization, ServerExceptionCode.CLIENT_UNAUTHORIZED);

        // Make sure the scopes are authorized
        Assert.isTrue(isAuthorized(Utils.explode(authorization.getScopes(), s -> s), pScopes),
                      ServerExceptionCode.SCOPE_UNAUTHORIZED);

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
