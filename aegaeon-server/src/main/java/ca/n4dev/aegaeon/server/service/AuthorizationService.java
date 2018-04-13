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

import ca.n4dev.aegaeon.api.exception.InvalidScopeException;
import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.repository.ClientRedirectionRepository;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.api.repository.UserAuthorizationRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * AuthorizationService.java
 *
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 9, 2017
 */
@Service
public class AuthorizationService {

    private ClientRepository clientRepository;
    private ClientRedirectionRepository clientRedirectionRepository;
    private UserAuthorizationRepository userAuthorizationRepository;
    private ScopeService scopeService;

    /**
     *
     * @param pClientRepository
     */
    @Autowired
    public AuthorizationService(UserAuthorizationRepository pUserAuthorizationRepository,
                                ClientRepository pClientRepository,
                                ClientRedirectionRepository pClientRedirectionRepository,
                                ScopeService pScopeService) {
        this.userAuthorizationRepository = pUserAuthorizationRepository;
        this.clientRepository = pClientRepository;
        this.clientRedirectionRepository = pClientRedirectionRepository;
        this.scopeService = pScopeService;
    }

    /**
     * Validate every aspect of an authorization request.
     * @param pAuthRequest
     * @param pRequestMethod
     * @param pClientPublicId
     * @param pRedirectionUrl
     * @param pScope
     */
    @Transactional(readOnly = true)
    public void validateAuthorizationRequest(AuthRequest pAuthRequest,
                                             RequestMethod pRequestMethod,
                                             String pClientPublicId,
                                             String pRedirectionUrl,
                                             String pScope) {

        // Required
        if (Utils.areOneEmpty(pClientPublicId, pRedirectionUrl, pScope) || pAuthRequest.getResponseTypes() == null || pAuthRequest.getResponseTypes().size() == 0) {
            throw new OauthRestrictedException(getClass(),
                                               OAuthErrorType.invalid_request,
                                               pAuthRequest,
                                               pClientPublicId,
                                               pRedirectionUrl,
                                               "One parameter is empty");
        }

        // Test method and param
        if (pRequestMethod != RequestMethod.GET && pRequestMethod != RequestMethod.POST) {
            throw new OAuthPublicRedirectionException(getClass(),
                                                      OAuthErrorType.invalid_request,
                                                      pAuthRequest,
                                                      pRedirectionUrl);
        }

        // Supported response type ?
        if (pAuthRequest.getResponseTypes() == null || pAuthRequest.getResponseTypes().size() == 0) {
            throw new OauthRestrictedException(getClass(),
                                               OAuthErrorType.invalid_request,
                                               pAuthRequest,
                                               pClientPublicId,
                                               pRedirectionUrl,
                                               "Invalid flow");
        }


        // Check redirection
        List<ClientRedirection> redirections = this.clientRedirectionRepository.findByClientPublicId(pClientPublicId);

        if (redirections == null || !Utils.isOneTrue(redirections, cr -> cr.getUrl().equals(pRedirectionUrl))) {
            throw new OauthRestrictedException(getClass(),
                                               OAuthErrorType.invalid_request,
                                               pAuthRequest,
                                               pClientPublicId,
                                               pRedirectionUrl,
                                               "Invalid redirect_uri.");
        }

        // Test Scopes
        try {
            List<Scope> scopes = this.scopeService.findScopeFromString(pScope);
        } catch (InvalidScopeException scex) {

            throw new OAuthPublicRedirectionException(getClass(),
                                                      OAuthErrorType.invalid_scope,
                                                      pAuthRequest,
                                                      pRedirectionUrl);
        }
    }


    public boolean isAuthorized(Authentication pAuthentication, String pClientPublicId) {

        if (Utils.isNotEmpty(pClientPublicId)
                && pAuthentication != null
                && pAuthentication.getPrincipal() instanceof AegaeonUserDetails) {

            Client client = this.clientRepository.findByPublicId(pClientPublicId);
            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();

            return this.userAuthorizationRepository.findByUserIdAndClientId(userDetails.getId(), client.getId()) != null;
        }

        return false;
    }
}
