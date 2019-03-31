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

import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.event.IntrospectEvent;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.IntrospectResponseView;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InstrospectService.java
 *
 * Service used to check an access token and return an Introspect response.
 *
 * @author by rguillemette
 * @since Dec 10, 2017
 */
@Service
public class InstrospectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrospectService.class);


    private AccessTokenService accessTokenService;
    private TokenFactory tokenFactory;
    private ServerInfo serverInfo;
    private ApplicationEventPublisher eventPublisher;

    /**
     * Default Constructor.
     * @param pAccessTokenService
     * @param pTokenFactory
     * @param pServerInfo
     */
    @Autowired
    public InstrospectService(AccessTokenService pAccessTokenService,
                              TokenFactory pTokenFactory,
                              ServerInfo pServerInfo,
                              ApplicationEventPublisher pEventPublisher) {
        this.accessTokenService = pAccessTokenService;
        this.tokenFactory = pTokenFactory;
        this.serverInfo = pServerInfo;
        this.eventPublisher = pEventPublisher;
    }

    /**
     * Perform an introspect.
     * @param pToken
     * @param pTokenHint
     * @param pAgentOfClientId
     * @param pAuthentication
     * @return
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public IntrospectResponseView introspect(String pToken,
                                             String pTokenHint, // ignored currently
                                             String pAgentOfClientId,
                                             Authentication pAuthentication) {


        final AegaeonUserDetails client = getClient(pAuthentication);
        final boolean introspectAllowed = isIntrospectAllowed(client);
        IntrospectResponseView response = new IntrospectResponseView(false);
        String tokenStatus = "invalid";
        String uniqueIdentifier = null;

        if (!Utils.areOneEmpty(pToken) && introspectAllowed) {

            try {

                // Is it a valid JWT ?
                SignedJWT.parse(pToken);

                // Try to get access token
                AccessToken accessToken = this.accessTokenService.findByToken(pToken);
                uniqueIdentifier = accessToken.getUser().getUniqueIdentifier();

                // Need to exist, be still valid, be a proper token and be the agent of client
                if (accessToken != null && this.tokenFactory.validate(accessToken.getClient(), pToken)) {

                    final boolean active = Utils.isAfterNow(accessToken.getValidUntil());
                    tokenStatus = active ? "active" : "inactive";

                    if (active) {

                        // Complete response
                        response.setActive(active);
                        response.setUsername(accessToken.getUser().getUserName());
                        response.setSub(uniqueIdentifier);
                        response.setClientId(accessToken.getClient().getPublicId());
                        response.setIssuer(this.serverInfo.getIssuer());
                        response.setScope(accessToken.getScopes());
                        response.setExpiration(accessToken.getValidUntil().toInstant().getEpochSecond());
                    }
                }

            } catch (Exception e) {
                // ignore
            }
        }

        raiseEvent(client, uniqueIdentifier, tokenStatus);

        return response;
    }

    private boolean isIntrospectAllowed(AegaeonUserDetails pAegaeonUserDetails) {

        if (pAegaeonUserDetails != null) {
            return pAegaeonUserDetails.isAllowIntrospection();
        }

        return false;
    }

    private AegaeonUserDetails getClient(Authentication pAuthentication) {
        if (pAuthentication != null && pAuthentication.getPrincipal() instanceof AegaeonUserDetails) {
            return (AegaeonUserDetails) pAuthentication.getPrincipal();
        }

        return null;
    }

    private void raiseEvent(AegaeonUserDetails pClient, String pUsername, String pResult) {

        eventPublisher.publishEvent(new IntrospectEvent(this,
                                                        pClient.getUsername(),
                                                        pClient.isAllowIntrospection(),
                                                        pUsername,
                                                        pResult));
    }
}
