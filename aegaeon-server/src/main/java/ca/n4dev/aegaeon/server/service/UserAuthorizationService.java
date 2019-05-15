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
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserAuthorization;
import ca.n4dev.aegaeon.api.repository.UserAuthorizationRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserAuthorizationService.java
 *
 * Manage {@link UserAuthorization} entity.
 *
 * @author by rguillemette
 * @since May 13, 2017
 */
@Service
public class UserAuthorizationService extends BaseSecuredService<UserAuthorization, UserAuthorizationRepository> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationService.class);

    private UserService userService;
    private ClientService clientService;
    private ScopeService scopeService;

    /**
     * @param pRepository
     */
    @Autowired
    public UserAuthorizationService(UserAuthorizationRepository pRepository,
                                    UserService pUserService,
                                    ClientService pClientService,
                                    ScopeService pScopeService) {
        super(pRepository);
        this.userService = pUserService;
        this.clientService = pClientService;
        this.scopeService = pScopeService;
    }

    @Transactional
    @PreAuthorize("#pUserDetails.id == principal.id")
    public void createOneUserAuthorization(AegaeonUserDetails pUserDetails, String pClientPublicId, String pScopes) {

        Assert.notNull(pUserDetails, ServerExceptionCode.USER_EMPTY);

        UserAuthorization ua = this.createUserAuthorization(pUserDetails.getId(), pClientPublicId, pScopes);

        if (ua == null) {
            throw new ServerException(ServerExceptionCode.UNEXPECTED_ERROR, "Unable to create UserAuthorization.");
        }

        LOGGER.info("Creating user authorization for {} / {}", pUserDetails.getId(), pClientPublicId);
    }

    @Transactional(readOnly = true)
    public boolean isAuthorized(Authentication pAuthentication,
                                String pClientPublicId,
                                String pClientRedirectionUrl,
                                String pRawScopeParam) {

        if (Utils.isNotEmpty(pClientPublicId)
                && pAuthentication != null
                && pAuthentication.getPrincipal() instanceof AegaeonUserDetails) {

            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();
            Client client = this.clientService.findByPublicId(pClientPublicId);

            if (client != null && userDetails != null) {

                // Check Url first
                if (Utils.isNotEmpty(pClientRedirectionUrl) && !checkRedirection(client.getId(), pClientRedirectionUrl)) {
                    return false;
                }

                UserAuthorization userAuthorization = getUserAuthorization(userDetails, client);

                if (userAuthorization == null) {
                    return false;
                }

                // finally, Validate scopes
                return scopeService.isPartOf(userAuthorization.getScopes(), pRawScopeParam);
            }
        }

        return false;
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = ClientService.CACHE_NAME,
            key = "{'isClientInfoValid', #pClientPublicId, pRedirectionUrl}")
    public boolean isClientInfoValid(String pClientPublicId, String pRedirectionUrl) {

        if (!Utils.areOneEmpty(pClientPublicId, pRedirectionUrl)) {

            // Get client by public id and check if exists
            Client client = this.clientService.findByPublicId(pClientPublicId);

            if (client != null) {
                // And then, check if it is a valid redirection
                return checkRedirection(client.getId(), pRedirectionUrl);
            }
        }

        return false;
    }

    private boolean checkRedirection(Long pClientId, String pRedirectionUrl) {
        if (pClientId != null && Utils.isNotEmpty(pRedirectionUrl)) {
            List<ClientRedirection> clientRedirections = clientService.findRedirectionsByClientId(pClientId);
            return Utils.isOneTrue(clientRedirections, cr -> cr.getUrl().equals(pRedirectionUrl));
        }
        return false;
    }

    private UserAuthorization getUserAuthorization(AegaeonUserDetails pUserDetails,
                                                   Client pClient) {
        if (pUserDetails != null && pClient != null) {

            if (pUserDetails.getId() != null) {
                return getRepository().findByUserIdAndClientId(pUserDetails.getId(),
                                                               pClient.getId());
            } else if (Utils.isNotEmpty(pUserDetails.getUsername())) {
                return getRepository().findByUserUserNameAndClientId(pUserDetails.getUsername(),
                                                                     pClient.getId());
            }
        }

        return null;
    }

    UserAuthorization createUserAuthorization(Long pUserId, String pClientPublicId, String pScopes) {

        Assert.notNull(pUserId, ServerExceptionCode.USER_EMPTY);
        Assert.notEmpty(pClientPublicId, ServerExceptionCode.CLIENT_EMPTY);

        Client client = this.clientService.findByPublicId(pClientPublicId);
        User user = this.userService.findById(pUserId);

        return createUserAuthorization(user, client, pScopes);
    }

    UserAuthorization createUserAuthorization(User pUser, Client pClient, String pScopes) {
        Assert.notNull(pUser, ServerExceptionCode.USER_EMPTY);
        Assert.notNull(pClient, ServerExceptionCode.CLIENT_EMPTY);

        // Validate Scopes
        Set<ScopeView> scopes = this.scopeService.getValidScopes(pScopes);

        UserAuthorization ua = new UserAuthorization(pUser, pClient, Utils.join(scopes, s -> s.getName()));

        return this.save(ua);
    }

    /**
     * Find a UserAuthorization by user and client.
     * @param pUserId The user's id.
     * @param pClientId The client's id.
     * @return A UserAuthorization or null.
     */
    UserAuthorization findByUserIdAndClientId(Long pUserId, Long pClientId) {
        return this.getRepository().findByUserIdAndClientId(pUserId, pClientId);
    }


}
