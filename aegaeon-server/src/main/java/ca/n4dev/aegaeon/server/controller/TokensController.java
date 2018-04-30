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

import ca.n4dev.aegaeon.api.exception.OpenIdException;
import ca.n4dev.aegaeon.api.exception.OpenIdExceptionBuilder;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.view.TokenResponse;

/**
 * OAuthTokensController.java
 * 
 * Controller managing /token endpoint and answering to auth_code and client_cred request.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Controller
@RequestMapping(value = TokensController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "oauth", havingValue = "true", matchIfMissing = true)
public class TokensController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TokensController.class);
    
    public static final String URL = "/token";
    
    private TokenServicesFacade tokenServicesFacade;
    private OpenIdEventLogger openIdEventLogger;

    /**
     * Default Constructor.
     * @param pTokenServicesFacade The token service facade.
     * @param pOpenIdEventLogger The event logger.
     */
    @Autowired
    public TokensController(TokenServicesFacade pTokenServicesFacade,
                            OpenIdEventLogger pOpenIdEventLogger) {
        
        this.tokenServicesFacade = pTokenServicesFacade;
        this.openIdEventLogger = pOpenIdEventLogger;
    }


    /**
     * 
     * @param pGrantType
     * @param pCode
     * @param pRedirectUri
     * @param pClientPublicId
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TokenResponse> token(
                    @RequestParam(value = "grant_type", required = false) String pGrantType,
                    @RequestParam(value = "code", required = false) String pCode,
                    @RequestParam(value = "redirect_uri", required = false) String pRedirectUri,
                    @RequestParam(value = "client_id", required = false) String pClientPublicId,
                    @RequestParam(value = "scope", required = false) String pScope, 
                    @RequestParam(value = "refresh_token", required = false) String pRefreshToken,
                    Authentication pAuthentication) {
        
        TokenResponse response = null;
        GrantType grantType = GrantType.from(pGrantType);

        try {

            if (grantType == GrantType.AUTHORIZATION_CODE) {
                response = authorizationCodeResponse(pCode, pRedirectUri, pClientPublicId, pAuthentication);
            } else if (grantType == GrantType.CLIENT_CREDENTIALS) {
                response = clientCredentialResponse(pAuthentication, pScope, pRedirectUri);
            } else if (grantType == GrantType.REFRESH_TOKEN) {
                response = refreshTokenResponse(pAuthentication, pRefreshToken);
            } else {
                throw new OpenIdException(ServerExceptionCode.GRANT_INVALID);
            }

            this.openIdEventLogger.log(OpenIdEvent.TOKEN_GRANTING, getClass(), pAuthentication.getName(), response);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (OpenIdException pOpenIdException) {
            // Add info and rethrow

            throw new OpenIdExceptionBuilder(pOpenIdException)
                        .clientId(pClientPublicId)
                        .redirection(pRedirectUri)
                        .from(grantType)
                        .build();

        } catch (Exception pException) {
            throw new OpenIdExceptionBuilder(pException)
                    .clientId(pClientPublicId)
                    .redirection(pRedirectUri)
                    .from(grantType)
                    .build();
        }


    }
    
    private TokenResponse clientCredentialResponse(Authentication pAuthentication,
                                                   String pScope,
                                                   String pRedirectUri) {
        
        TokenResponse token = this.tokenServicesFacade.createTokenForClientCred(pScope, pRedirectUri, pAuthentication);
        
        return token;
    }
    
    private TokenResponse refreshTokenResponse(Authentication pAuthentication, 
                                               String pRefreshToken) {
        
        TokenResponse token = this.tokenServicesFacade.createTokenForRefreshToken(pRefreshToken, pAuthentication);

        return token;
    }
    
    /**
     * Check and build an authorization code response.
     * @param pCode
     * @param pRedirectUri
     * @param pClientPublicId
     * @return
     */
    private TokenResponse authorizationCodeResponse(
                                           String pCode,
                                           String pRedirectUri,
                                           String pClientPublicId, 
                                           Authentication pAuthentication) {
        
        TokenResponse token = this.tokenServicesFacade.createTokenForAuthCode(pClientPublicId, 
                                                                              pCode, 
                                                                              pRedirectUri, 
                                                                              pAuthentication);
            
        return token;
        
    }
}
