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

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.repository.ClientRedirectionRepository;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.api.repository.UserAuthorizationRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthorizationService.java
 * <p>
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


    @Transactional(readOnly = true)
    public boolean isAuthorized(Authentication pAuthentication, String pClientPublicId) {
        return isAuthorized(pAuthentication, pClientPublicId, null);
    }

    @Transactional(readOnly = true)
    public boolean isAuthorized(Authentication pAuthentication, String pClientPublicId, String pClientRedirectionUrl) {

        if (Utils.isNotEmpty(pClientPublicId)
                && pAuthentication != null
                && pAuthentication.getPrincipal() instanceof AegaeonUserDetails) {

            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();
            Client client = this.clientRepository.findByPublicId(pClientPublicId);

            if (client != null && userDetails != null) {

                // Check Url first
                if (Utils.isNotEmpty(pClientRedirectionUrl) && !checkRedirection(client.getId(), pClientRedirectionUrl)) {
                    return false;
                }

                // Check if the user has allowed this client
                return this.userAuthorizationRepository.findByUserIdAndClientId(userDetails.getId(), client.getId()) != null;
            }

        }

        return false;
    }

    @Transactional(readOnly = true)
    public boolean isClientInfoValid(String pClientPublicId, String pRedirectionUrl) {

        if (!Utils.areOneEmpty(pClientPublicId, pRedirectionUrl)) {

            // Get client by public id and check if exists
            Client client = this.clientRepository.findByPublicId(pClientPublicId);

            if (client != null) {
                // And then, check if it is a valid redirection
                return checkRedirection(client.getId(), pRedirectionUrl);
            }
        }

        return false;
    }

    private boolean checkRedirection(Long pClientId, String pRedirectionUrl) {
        if (pClientId != null && Utils.isNotEmpty(pRedirectionUrl)) {
            List<ClientRedirection> clientRedirections = clientRedirectionRepository.findByClientId(pClientId);
            return Utils.isOneTrue(clientRedirections, cr -> cr.getUrl().equals(pRedirectionUrl));
        }
        return false;
    }
}
