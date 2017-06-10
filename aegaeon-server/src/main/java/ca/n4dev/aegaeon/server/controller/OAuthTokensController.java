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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.n4dev.aegaeon.api.exception.InvalidScopeException;
import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicJsonException;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.protocol.AuthorizationGrant;
import ca.n4dev.aegaeon.server.controller.dto.TokenResponse;
import ca.n4dev.aegaeon.server.model.AuthorizationCode;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.ClientType;
import ca.n4dev.aegaeon.server.model.RefreshToken;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.BaseTokenService;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.RefreshTokenService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * OAuthTokensController.java
 * 
 * Controller managing /token endpoint and answering to auth_code and client_cred request.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Controller
@RequestMapping(value = OAuthTokensController.URL)
public class OAuthTokensController extends BaseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokensController.class);
    
    public static final String URL = "/token";
    
    private ClientService clientService;
    private AuthorizationCodeService authorizationCodeService;
    private ScopeService scopeService;
    private TokenServicesFacade tokenServicesFacade;
    private RefreshTokenService refreshTokenService;
    
    /**
     * Default Constructor.
     * @param pClientService Service used to access client config.
     * @param pAuthorizationCodeService Service used to access auth code.
     */
    @Autowired
    public OAuthTokensController(ClientService pClientService,
                                 AuthorizationCodeService pAuthorizationCodeService,
                                 TokenServicesFacade pTokenServicesFacade,
                                 ScopeService pScopeService,
                                 RefreshTokenService pRefreshTokenService) {
        
        this.clientService = pClientService;
        this.authorizationCodeService = pAuthorizationCodeService;
        this.scopeService = pScopeService;
        this.tokenServicesFacade = pTokenServicesFacade;
        this.refreshTokenService = pRefreshTokenService;
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
        

        if (AuthorizationGrant.is(pGrantType, AuthorizationGrant.AUTHORIZATIONCODE)) {
            return authorizationCodeResponse(pCode, pRedirectUri, pClientPublicId);
        } else if (AuthorizationGrant.is(pGrantType, AuthorizationGrant.CLIENTCREDENTIALS)) {
            return clientCredentialResponse(pAuthentication, pScope, pRedirectUri);
        } else if (AuthorizationGrant.is(pGrantType, AuthorizationGrant.REFRESH_TOKEN)) {
            return refreshTokenResponse(pAuthentication, pRefreshToken);
        } else {
            throw new OauthRestrictedException(AuthorizationGrant.AUTHORIZATIONCODE, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Wrong grant_type.");
        }
    }
    
    private ResponseEntity<TokenResponse> clientCredentialResponse(Authentication pAuthentication,
                                                                   String pScope,
                                                                   String pRedirectUri) {
        
        SpringAuthUserDetails auth = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        // Load client 
        Client client = this.clientService.findById(auth.getId());
        
        if (client == null) {
            throw new OauthRestrictedException(AuthorizationGrant.CLIENTCREDENTIALS, 
                    OAuthErrorType.invalid_request, 
                    auth.getUsername(), 
                    null,
                    "Invalid client or incorrect public id.");
        }
        
        // Did we set this client to use this flow
        if (client.getGrantType() != AuthorizationGrant.CLIENTCREDENTIALS) {
            throw new OAuthPublicJsonException(AuthorizationGrant.CLIENTCREDENTIALS, 
                                               OAuthErrorType.unsupported_response_type);
        }
        
        try {
            List<Scope> scopes = this.scopeService.findScopeFromString(pScope);
            
            TokenResponse token = this.tokenServicesFacade.createTokenResponse(AuthorizationGrant.AUTHORIZATIONCODE, 
                    client.getPublicId(), 
                    auth.getId(),
                    scopes, 
                    pRedirectUri);

            return new ResponseEntity<TokenResponse>(token, HttpStatus.OK);
            
            // TODO(RG): catch serverexception and rethrow
        } catch (InvalidScopeException e) {
            throw new OAuthPublicJsonException(AuthorizationGrant.CLIENTCREDENTIALS, OAuthErrorType.invalid_scope);
        } 
    }
    
    private ResponseEntity<TokenResponse> refreshTokenResponse(Authentication pAuthentication, 
                                                               String pRefreshToken) {
        
        if (Utils.isEmpty(pRefreshToken)) {
            throw new OAuthPublicJsonException(AuthorizationGrant.REFRESH_TOKEN, OAuthErrorType.invalid_request);
        }
        
        SpringAuthUserDetails auth = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        Client client = this.clientService.findById(auth.getId());

        if (client == null) {
            throw new OauthRestrictedException(AuthorizationGrant.REFRESH_TOKEN, 
                    OAuthErrorType.invalid_request, 
                    auth.getUsername(), 
                    null,
                    "Invalid client or incorrect public id.");
        }
        
        // Check if the client has the proper scope
        if (!client.getScopesAsNameList().contains(BaseTokenService.OFFLINE_SCOPE)) {
            throw new OauthRestrictedException(AuthorizationGrant.REFRESH_TOKEN, 
                    OAuthErrorType.invalid_scope, 
                    auth.getUsername(), 
                    null,
                    "This client don't have the offline access scope.");
        }
        
        // Load the refresh_token
        RefreshToken rftoken = this.refreshTokenService.findByTokenValueAndClientId(pRefreshToken, client.getId());
        
        if (rftoken == null || !Utils.isStillValid(rftoken.getValidUntil())) {
            throw new OAuthPublicJsonException(AuthorizationGrant.REFRESH_TOKEN, OAuthErrorType.invalid_grant);
        }
        
        // Ok, the token is valid, so create a new access token
        String scopesStr = rftoken.getScopes();
        List<Scope> scopes = this.scopeService.findScopeFromString(scopesStr, BaseTokenService.OFFLINE_SCOPE);
        TokenResponse token = this.tokenServicesFacade.createTokenResponse(client.getGrantType(), 
                                        client.getPublicId(), 
                                        rftoken.getUser().getId(), 
                                        scopes, 
                                        null);

        return new ResponseEntity<TokenResponse>(token, HttpStatus.OK);
    }
    
    /**
     * Check and build an authorization code response.
     * @param pGrantType
     * @param pCode
     * @param pRedirectUri
     * @param pClientPublicId
     * @return
     */
    private ResponseEntity<TokenResponse> authorizationCodeResponse(
                                           String pCode,
                                           String pRedirectUri,
                                           String pClientPublicId) {
        
        // Required
        if (Utils.areOneEmpty(pCode, pRedirectUri, pClientPublicId)) {
            throw new OauthRestrictedException(AuthorizationGrant.AUTHORIZATIONCODE, 
                                               OAuthErrorType.invalid_request, 
                                               pClientPublicId, 
                                               pRedirectUri,
                                               "One parameter is empty");
        }
        
        // Load client 
        Client client = this.clientService.findByPublicId(pClientPublicId);
        if (client == null) {
            throw new OauthRestrictedException(AuthorizationGrant.AUTHORIZATIONCODE, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid client or incorrect public id.");
        }
        
        // Check redirection
        if (!client.hasRedirection(pRedirectUri)) {
            throw new OauthRestrictedException(AuthorizationGrant.AUTHORIZATIONCODE, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid redirect_uri.");
        }
        
        // Did we set this client to use this flow
        if (client.getGrantType() != AuthorizationGrant.AUTHORIZATIONCODE) {
            throw new OAuthPublicRedirectionException(AuthorizationGrant.AUTHORIZATIONCODE, OAuthErrorType.unauthorized_client, pRedirectUri);
        }
        
        // Ok, check the code now
        AuthorizationCode authCode = this.authorizationCodeService.findByCode(pCode);
        if (authCode == null || !Utils.isStillValid(authCode.getValidUntil())) {
            throw new OAuthPublicRedirectionException(AuthorizationGrant.AUTHORIZATIONCODE, OAuthErrorType.access_denied, pRedirectUri);
        }
        
        // Make sure the redirection is the same than previously
        if (!authCode.getRedirectUrl().equals(pRedirectUri)) {
            throw new OauthRestrictedException(AuthorizationGrant.AUTHORIZATIONCODE, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid redirect_uri.");
        }
        
        try {
            // Ok, good to go here, so create an access token and delete the auth code.
            List<Scope> scopes = this.scopeService.findScopeFromString(authCode.getScopes());
            
            TokenResponse token = this.tokenServicesFacade.createTokenResponse(AuthorizationGrant.AUTHORIZATIONCODE, 
                                                                               pClientPublicId, 
                                                                               authCode.getUserId(), 
                                                                               scopes, 
                                                                               pRedirectUri);
            
            return new ResponseEntity<TokenResponse>(token, HttpStatus.OK);
            
        } finally {
            // Delete the auth code in all situation
            try {
                this.authorizationCodeService.delete(authCode);
            } catch (Exception e) {
                LOGGER.error("Unable to delete auth code", e);
            }
        }
        
    }
}
