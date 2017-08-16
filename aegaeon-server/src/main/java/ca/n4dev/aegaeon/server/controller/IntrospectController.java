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
package ca.n4dev.aegaeon.server.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jwt.SignedJWT;

import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.controller.dto.IntrospectResponse;
import ca.n4dev.aegaeon.server.service.AccessTokenService;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * IntrospectController.java
 * 
 * Introspect controller used to verify a token.
 *
 * @author by rguillemette
 * @since Aug 10, 2017
 */
@Controller
@RequestMapping(value = IntrospectController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "introspect", havingValue = "true", matchIfMissing = false)
public class IntrospectController {
    
    public static final String URL = "/introspect";
    
    private AccessTokenService accessTokenService;
    private TokenFactory tokenFactory;
    private ServerInfo serverInfo;
    private OpenIdEventLogger openIdEventLogger;
    
    /**
     * Default Constructor.
     * @param pClientService The service to access client.
     * @param pAccessTokenService The service to access access token entity.
     * @param pTokenFactory The token factory to validate token.
     * @param pServerInfo This server info.
     */
    public IntrospectController(AccessTokenService pAccessTokenService,
                                TokenFactory pTokenFactory,
                                ServerInfo pServerInfo,
                                OpenIdEventLogger pOpenIdEventLogger) {
        this.accessTokenService = pAccessTokenService;
        this.tokenFactory = pTokenFactory;
        this.serverInfo = pServerInfo;
        this.openIdEventLogger = pOpenIdEventLogger;
    }
    
    /**
     * Introspect (verify) a token to know if it is still valid.
     * @param pAuthentication The client authentication object.
     * @return A userinfo json object.
     */
    @RequestMapping(value = "", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<IntrospectResponse> introspect(
                                       @RequestParam(value = "token", required = false) String pToken, 
                                       @RequestParam(value = "token_hint", required = false) String pTokenHint, // ignored currently
                                       @RequestParam(value = "agent_of_client_id", required = false) String pAgentOfClientId,
                                       Authentication pAuthentication) {
        
        IntrospectResponse response = new IntrospectResponse(false);
        
        if (!Utils.areOneEmpty(pToken, pAgentOfClientId)) {
            
            try {
                
                // Is it a valid JWT ?
                SignedJWT.parse(pToken);
                
                // Try to get access token
                AccessToken accessToken = this.accessTokenService.findByTokenValue(pToken);
                
                // Exists ?
                if (accessToken == null) {
                    return ResponseEntity.ok(response);
                }
                
                // Still Valid ?
                if (!Utils.isAfterNow(accessToken.getValidUntil())) {
                    return ResponseEntity.ok(response);
                }
                
                // Validate
                if (!this.tokenFactory.validate(accessToken.getClient(), pToken)) {
                    return ResponseEntity.ok(response);
                }
                
                // Check if the introspect client is validating for the right client
                if (!accessToken.getClient().getPublicId().equals(pAgentOfClientId)) {
                    return ResponseEntity.ok(response);
                }
                
                // OK, good
                User u = accessToken.getUser();
                
                // Complete response
                response.setActive(true);
                response.setUsername(u.getUserName());
                response.setSub(u.getUniqueIdentifier());
                response.setClientId(accessToken.getClient().getPublicId());
                response.setIssuer(this.serverInfo.getIssuer());
                response.setScope(accessToken.getScopes());

                openIdEventLogger.log(OpenIdEvent.REQUEST_INFO, getClass(), u.getUserName(), null);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                openIdEventLogger.log(OpenIdEvent.OTHERS, getClass(), e.getMessage());
            }
            
        }
        
        return ResponseEntity.ok(new IntrospectResponse(false));
    }
}
