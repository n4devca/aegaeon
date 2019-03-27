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
import java.util.Map;
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserAuthorization;
import ca.n4dev.aegaeon.api.repository.UserAuthorizationRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private UserService userService;
    private ClientService clientService;
    private ScopeService scopeService;
    private OpenIdEventLogger openIdEventLogger;
    
    /**
     * @param pRepository
     */
    @Autowired
    public UserAuthorizationService(UserAuthorizationRepository pRepository, 
                                    UserService pUserService, 
                                    ClientService pClientService, 
                                    ScopeService pScopeService,
                                    OpenIdEventLogger pOpenIdEventLogger) {
        super(pRepository);
        this.userService = pUserService;
        this.clientService = pClientService;
        this.scopeService = pScopeService;
        this.openIdEventLogger = pOpenIdEventLogger;
    }

    @Transactional
    @PreAuthorize("#pUserDetails.id == principal.id")
    public void createOneUserAuthorization(AegaeonUserDetails pUserDetails, String pClientPublicId, String pScopes) {
        
        Assert.notNull(pUserDetails, ServerExceptionCode.USER_EMPTY);
        
        UserAuthorization ua = this.createUserAuthorization(pUserDetails.getId(), pClientPublicId, pScopes);
        
        if (ua == null) {
            throw new ServerException(ServerExceptionCode.UNEXPECTED_ERROR, "Unable to create UserAuthorization.");
        }
        
        this.openIdEventLogger.log(OpenIdEvent.AUTHORIZATION, getClass(), pUserDetails.getUsername(), ua);
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
    
    @Transactional(readOnly = true)
    public boolean isAuthorized(Long pUserId, Long pClientId) {
        return this.getRepository().findByUserIdAndClientId(pUserId, pClientId) != null;
    }
    
}
