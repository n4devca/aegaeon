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

import java.time.ZoneOffset;

import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.controller.exception.InvalidIntrospectException;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.IntrospectResponseView;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InstrospectService.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 10, 2017
 */
@Service
public class InstrospectService {
    
    private AccessTokenService accessTokenService;
    private TokenFactory tokenFactory;
    private ServerInfo serverInfo;
    private OpenIdEventLogger openIdEventLogger;
    
    /**
     * Default Constructor.
     * @param pAccessTokenService
     * @param pTokenFactory
     * @param pServerInfo
     * @param pOpenIdEventLogger
     */
    @Autowired
    public InstrospectService(AccessTokenService pAccessTokenService,
                              TokenFactory pTokenFactory, 
                              ServerInfo pServerInfo, 
                              OpenIdEventLogger pOpenIdEventLogger) {
        this.accessTokenService = pAccessTokenService;
        this.tokenFactory = pTokenFactory;
        this.serverInfo = pServerInfo;
        this.openIdEventLogger = pOpenIdEventLogger;
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
        
        IntrospectResponseView response = new IntrospectResponseView(false);
        
        if (!Utils.areOneEmpty(pToken, pAgentOfClientId)) {
            
            try {
                // Is it a valid JWT ?
                SignedJWT.parse(pToken);
                
                // Try to get access token
                AccessToken accessToken = this.accessTokenService.findByToken(pToken);
                
                // Need to exist, be still valid, be a proper token and be the agent of client
                if (accessToken != null 
                        && Utils.isAfterNow(accessToken.getValidUntil()) 
                        && this.tokenFactory.validate(accessToken.getClient(), pToken) 
                        && accessToken.getClient().getPublicId().equals(pAgentOfClientId)) {
                    
                    
                    // OK, good
                    User u = accessToken.getUser();
                    
                    // Complete response
                    response.setActive(true);
                    response.setUsername(u.getUserName());
                    response.setSub(u.getUniqueIdentifier());
                    response.setClientId(accessToken.getClient().getPublicId());
                    response.setIssuer(this.serverInfo.getIssuer());
                    response.setScope(accessToken.getScopes());
                    response.setExpiration(accessToken.getValidUntil().toInstant(ZoneOffset.UTC).getEpochSecond());
                    
                    openIdEventLogger.log(OpenIdEvent.REQUEST_INFO, getClass(), u.getUserName(), null);
                }

            } catch (Exception e) {
                openIdEventLogger.log(OpenIdEvent.OTHERS, getClass(), e.getMessage());
            }
        } else {
            throw new InvalidIntrospectException();
        }

        
        return response;
    }
}
