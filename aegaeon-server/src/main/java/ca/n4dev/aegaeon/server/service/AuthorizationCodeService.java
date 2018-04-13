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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.AuthorizationCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.repository.AuthorizationCodeRepository;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.AuthorizationCodeView;
import ca.n4dev.aegaeon.server.view.mapper.AuthorizationCodeViewMapper;

/**
 * AuthorizationCodeService.java
 * 
 * A service managing AuthorizationCode.
 *
 * @author by rguillemette
 * @since May 10, 2017
 */
@Service
public class AuthorizationCodeService extends BaseSecuredService<AuthorizationCode, AuthorizationCodeRepository> {

    private TokenFactory tokenFactory;
    private UserRepository userRepository;
    private ClientRepository clientRepository;
    private ScopeService scopeService;
    private AuthorizationCodeViewMapper viewMapper;
    
    /**
     * Default Constructor.
     * @param pRepository Service repository.
     */
    @Autowired
    public AuthorizationCodeService(AuthorizationCodeRepository pRepository, 
                                    TokenFactory pTokenFactory,
                                    UserRepository pUserRepository,
                                    ClientRepository pClientRepository,
                                    ScopeService pScopeService,
                                    AuthorizationCodeViewMapper pViewMapper) {
        super(pRepository);
        
        this.tokenFactory = pTokenFactory;
        this.userRepository = pUserRepository;
        this.clientRepository = pClientRepository;
        this.scopeService = pScopeService;
        this.viewMapper = pViewMapper;
    }
    
    @Transactional
    @PreAuthorize("isAuthenticated() and principal.id == #pUserId")
    public AuthorizationCodeView createCode(Long pUserId, String pClientPublicId, String pResponseType, String pScopes, String pRedirectUrl) {
        // Get Scopes
        List<Scope> scopes = this.scopeService.findScopeFromString(pScopes);
        
        // Create Code
        AuthorizationCode code = createCode(pUserId, pClientPublicId, pResponseType, scopes, pRedirectUrl);
        
        // Return view
        return this.viewMapper.toView(code);
    }


    /**
     * Find a AuthCode by code (string)
     * @param pCode The code.
     * @return An {@link AuthorizationCode} or null.
     */
    AuthorizationCode findByCode(String pCode) {
        return this.getRepository().findByCode(pCode);
    }
    
    
    AuthorizationCode createCode(Long pUserId, String pClientPublicId, String pResponseType, List<Scope> pScopes, String pRedirectUrl) {
        
        Assert.notNull(pUserId, ServerExceptionCode.USER_EMPTY);
        Assert.notEmpty(pClientPublicId, ServerExceptionCode.CLIENT_EMPTY);
        
        //User user = this.userRepository.findOne(pUserId);
        Optional<User> u = this.userRepository.findById(pUserId);
        Client client = this.clientRepository.findByPublicId(pClientPublicId);
        
        return createCode(u.orElse(null), client, pResponseType, pScopes, pRedirectUrl);
    }
    
    
    /**
     * Create an {@link AuthorizationCode} for a user and a client.
     * @param pUser The user.
     * @param pClient The client.
     * @return A code or null.
     */
    AuthorizationCode createCode(User pUser, Client pClient, String pResponseType, List<Scope> pScopes, String pRedirectUrl) {
        Assert.notNull(pUser, ServerExceptionCode.USER_EMPTY);
        Assert.notNull(pClient, ServerExceptionCode.CLIENT_EMPTY);
        Assert.notEmpty(pRedirectUrl, ServerExceptionCode.CLIENT_REDIRECTURL_EMPTY);
        Assert.notEmpty(pResponseType, ServerExceptionCode.RESPONSETYPE_INVALID);

        AuthorizationCode c = new AuthorizationCode();
        c.setClient(pClient);
        c.setUser(pUser);
        c.setCode(this.tokenFactory.uniqueCode());
        c.setResponseType(pResponseType);
        c.setValidUntil(LocalDateTime.now().plus(3L, ChronoUnit.MINUTES));
        c.setRedirectUrl(pRedirectUrl);
        
        if (pScopes != null) {
            c.setScopes(Utils.join(" ", pScopes, s -> s.getName()));
        }

        return this.getRepository().save(c);
    }
    
    /**
     * Delete an auth code.
     * @param pAuthorizationCodeId The auth code id.
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorizationCodeService.hasPermissionTo(principal.id, #pAuthorizationCodeId, 'DELETE')")
    public void delete(Long pAuthorizationCodeId) {
        this.getRepository().deleteById(pAuthorizationCodeId);
    }

    /**
     * Check if a user is allowed to do an operation.
     * @param pAuthUserId
     * @param pId
     * @param pOperation
     * @return
     */
    @Transactional(readOnly = true)
    public boolean hasPermissionTo(Long pAuthUserId, Long pId, String pOperation) {
        if (pId != null && pAuthUserId != null && Utils.isNotEmpty(pOperation)) {
            AuthorizationCode code = this.findById(pId);
            
            if (code != null && code.getUserId().equals(pAuthUserId)) {
                return true;
            }
        }
        
        return false;
    }
}
