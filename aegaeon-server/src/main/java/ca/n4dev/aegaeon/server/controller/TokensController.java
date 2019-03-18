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
package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.api.exception.ErrorHandling;
import ca.n4dev.aegaeon.api.exception.OpenIdExceptionBuilder;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.server.controller.exception.BaseException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidAuthorizationCodeException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidClientIdException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidClientRedirectionException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidGrantTypeException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidRequestMethodException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidScopeException;
import ca.n4dev.aegaeon.server.service.AuthorizationService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.TokenResponse;
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

/**
 * OAuthTokensController.java
 * <p>
 * Controller managing /token endpoint and answering to auth_code and client_cred request.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Controller
@RequestMapping(value = TokensController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "oauth", havingValue = "true", matchIfMissing = true)
public class TokensController {

    public static final String URL = "/token";
    private static final Logger LOGGER = LoggerFactory.getLogger(TokensController.class);
    private TokenServicesFacade tokenServicesFacade;
    private AuthorizationService authorizationService;
    private ScopeService scopeService;
    private OpenIdEventLogger openIdEventLogger;

    /**
     * Default Constructor.
     *
     * @param pTokenServicesFacade The token service facade.
     * @param pOpenIdEventLogger   The event logger.
     */
    @Autowired
    public TokensController(AuthorizationService pAuthorizationService,
                            ScopeService pScopeService,
                            TokenServicesFacade pTokenServicesFacade,
                            OpenIdEventLogger pOpenIdEventLogger) {

        authorizationService = pAuthorizationService;
        scopeService = pScopeService;
        tokenServicesFacade = pTokenServicesFacade;
        openIdEventLogger = pOpenIdEventLogger;
    }


    /**
     * @param pGrantType
     * @param pCode
     * @param pRedirectUri
     * @param pClientPublicId
     * @return
     */
    @RequestMapping(value = "")
    @ResponseBody
    public ResponseEntity<TokenResponse> token(
            @RequestParam(value = "grant_type", required = false) String pGrantType,
            @RequestParam(value = "code", required = false) String pCode,
            @RequestParam(value = "redirect_uri", required = false) String pRedirectUri,
            @RequestParam(value = "client_id", required = false) String pClientPublicId,
            @RequestParam(value = "scope", required = false) String pScope,
            @RequestParam(value = "refresh_token", required = false) String pRefreshToken,
            Authentication pAuthentication,
            RequestMethod pRequestMethod) {

        TokenResponse response = null;

        String clientPublicId = Utils.coalesce(pClientPublicId, pAuthentication.getName());

        TokenRequest tokenRequest = new TokenRequest(pGrantType, pCode, clientPublicId, pRedirectUri, pScope, pRefreshToken);

        // -- Validations

        // Client
        Assert.notEmpty(clientPublicId, () -> new InvalidClientIdException(tokenRequest));
        Assert.notEmpty(pRedirectUri, () -> new InvalidClientRedirectionException(tokenRequest));

        // Make sure the client and redirection is valid
        if (!authorizationService.isClientInfoValid(clientPublicId, pRedirectUri)) {
            throw new InvalidClientRedirectionException(tokenRequest);
        }

        // Request Method
        Assert.isTrue(pRequestMethod == RequestMethod.POST, () -> new InvalidRequestMethodException(tokenRequest, pRequestMethod));

        // Grant Type
        Assert.isTrue(isAcceptableGrantType(tokenRequest.getGrantTypeAsType()),
                      () -> new InvalidGrantTypeException(tokenRequest));

        if (GrantType.AUTHORIZATION_CODE == tokenRequest.getGrantTypeAsType()) {
            Assert.notEmpty(pCode, () -> new InvalidAuthorizationCodeException(tokenRequest, "empty"));
            Assert.isEmpty(pScope, () -> new InvalidScopeException(pScope, tokenRequest));
        } else {
            // Others needs scopes
            Assert.notEmpty(pScope, () -> new InvalidScopeException(pScope, tokenRequest));
        }


        // Scopes


        // --


        try {

            switch (tokenRequest.getGrantTypeAsType()) {

                case AUTHORIZATION_CODE:
                    response = authorizationCodeResponse(tokenRequest, pAuthentication);
                    break;
                case REFRESH_TOKEN:
                    response = refreshTokenResponse(tokenRequest, pAuthentication);
                    break;
                case CLIENT_CREDENTIALS:
                    response = clientCredentialResponse(tokenRequest, pAuthentication);
                    break;
                default:
                    throw new InvalidGrantTypeException(tokenRequest);
            }

            this.openIdEventLogger.log(OpenIdEvent.TOKEN_GRANTING, getClass(), pAuthentication.getName(), response);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (BaseException pBaseException) {
            throw pBaseException;
        } catch (Exception pException) {
            // TODO(RG): change that!
            throw new OpenIdExceptionBuilder(pException)
                    .clientId(clientPublicId)
                    .redirection(pRedirectUri)
                    .from(tokenRequest.getGrantTypeAsType())
                    .handling(ErrorHandling.JSON)
                    .build();
        }


    }

    private TokenResponse clientCredentialResponse(TokenRequest pTokenRequest,
                                                   Authentication pAuthentication) {

        TokenResponse token = this.tokenServicesFacade.createTokenForClientCred(pTokenRequest, pAuthentication);

        return token;
    }

    private TokenResponse refreshTokenResponse(TokenRequest pTokenRequest, Authentication pAuthentication) {

        TokenResponse token = this.tokenServicesFacade.createTokenForRefreshToken(pTokenRequest, pAuthentication);

        return token;
    }

    /**
     * Check and build an authorization code response.
     *
     * @param pTokenRequest
     * @param pAuthentication
     * @return
     */
    private TokenResponse authorizationCodeResponse(TokenRequest pTokenRequest,
                                                    Authentication pAuthentication) {

        TokenResponse token = this.tokenServicesFacade.createTokenForAuthCode(pTokenRequest,
                                                                              pAuthentication);

        return token;

    }

    private boolean isAcceptableGrantType(GrantType pGrantType) {
        return pGrantType == GrantType.AUTHORIZATION_CODE ||
                pGrantType == GrantType.CLIENT_CREDENTIALS ||
                pGrantType == GrantType.REFRESH_TOKEN;
    }
}
