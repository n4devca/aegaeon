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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicJsonException;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.AuthorizationCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientGrantType;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.TokenResponse;

/**
 * TokensService.java
 * 
 * TokensService acts as a facade for AccessTokenService and RefreshTokenService and 
 * provide an easier way to deal with token to controllers.
 *
 * @author by rguillemette
 * @since Jun 3, 2017
 */
@Service
public class TokenServicesFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServicesFacade.class);

    private IdTokenService idTokenService;
    private AccessTokenService accessTokenService;
    private RefreshTokenService refreshTokenService;
    private ScopeService scopeService;
    private AuthorizationCodeService authorizationCodeService;
    
    private ClientService clientService;
    
    @Autowired
    public TokenServicesFacade(IdTokenService pIdTokenService, 
                               AccessTokenService pAccessTokenService, 
                               RefreshTokenService pRefreshTokenService,
                               ScopeService pScopeService, 
                               AuthorizationCodeService pAuthorizationCodeService,
                               ClientService pClientService
                               ) {
        
        this.idTokenService = pIdTokenService;
        this.accessTokenService = pAccessTokenService;
        this.refreshTokenService = pRefreshTokenService;
        this.scopeService = pScopeService;
        this.authorizationCodeService = pAuthorizationCodeService;

        this.clientService = pClientService;
    }
    
    
    @Transactional
    public TokenResponse createTokenForAuthCode(String pClientPublicId, 
                                                String pCode,
                                                String pRedirectUri,
                                                Authentication pAuthentication) {
        
        Flow flow = FlowFactory.authCode();
        // Need auth
        validateAuthentication(pAuthentication, flow);
        
        AuthorizationCode authCode = null;
        validateAuthorizationCodeRequest(pCode, pRedirectUri, pClientPublicId, pAuthentication);
        
        // Valid from here
        
        try {
            authCode = this.authorizationCodeService.findByCode(pCode);
            
            return createTokenResponse(flow, 
                    authCode.getClient().getPublicId(), 
                    authCode.getScopes(), 
                    authCode.getRedirectUrl(), 
                    pAuthentication);
            
        } finally {
            // Always delete the authorization code.
            try {
                if (authCode != null) {
                    this.authorizationCodeService.delete(authCode.getId());                    
                }
            } catch (Exception e) {
                LOGGER.error("Unable to delete auth code", e);
            }
        }
        
    }
    
    
    private void validateAuthorizationCodeRequest(String pCode,
                                                 String pRedirectUri,
                                                 String pClientPublicId,
                                                 Authentication pAuthentication) {
        Flow flow = FlowFactory.authCode();
        
        // Required
        if (Utils.areOneEmpty(pCode, pRedirectUri, pClientPublicId)) {
            throw new OauthRestrictedException(getClass(),
                                               flow, 
                                               OAuthErrorType.invalid_request, 
                                               pClientPublicId, 
                                               pRedirectUri,
                                               "One parameter is empty");
        }
        
        // Load client 
        Client client = this.clientService.findByPublicId(pClientPublicId);
        if (client == null) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid client or incorrect public id.");
        }
        
        // Check redirection
        List<ClientRedirection> redirections = this.clientService.findRedirectionsByclientId(client.getId());
        
        if (redirections == null || !Utils.isOneTrue(redirections, cr -> cr.getUrl().equals(pRedirectUri))) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid redirect_uri.");
        }
        
        // Did we set this client to use this flow
        List<ClientGrantType> grants = this.clientService.findGrantTypesByclientId(client.getId());
        
        if (!Utils.isOneTrue(grants, g -> g.getGrantType().getCode().equals(GrantType.CODE_AUTH_CODE))) {
            
            throw new OAuthPublicRedirectionException(getClass(),
                    flow, OAuthErrorType.unauthorized_client, pRedirectUri);
        }
        
        // Ok, check the code now
        AuthorizationCode authCode = this.authorizationCodeService.findByCode(pCode);
        if (authCode == null || !Utils.isAfterNow(authCode.getValidUntil())) {
            throw new OAuthPublicRedirectionException(getClass(),
                    flow, OAuthErrorType.access_denied, pRedirectUri);
        }
        
        // Make sure, it's the same client
        if (!authCode.getClient().getPublicId().equals(pClientPublicId)) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid client or incorrect public id.");
        }
        
        // Make sure the redirection is the same than previously
        if (!authCode.getRedirectUrl().equals(pRedirectUri)) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectUri,
                    "Invalid redirect_uri.");
        }
        
    }
    
    @Transactional
    public TokenResponse createTokenForRefreshToken(String pRefreshToken, Authentication pAuthentication) {
        Flow flow = FlowFactory.refreshToken();
        
        if (Utils.isEmpty(pRefreshToken)) {
            throw new OAuthPublicJsonException(getClass(),
                    flow, OAuthErrorType.invalid_request);
        }

        validateAuthentication(pAuthentication, flow);
        SpringAuthUserDetails auth = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        Client client = this.clientService.findById(auth.getId());

        if (client == null) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    auth.getUsername(), 
                    null,
                    "Invalid client or incorrect public id.");
        }
        
        
        
        // Check if the client has the proper scope
        List<ClientScope> clientScopes = this.clientService.findScopeByClientId(client.getId());
        if (!Utils.isOneTrue(clientScopes, cs -> cs.getScope().getName().equals(BaseTokenService.OFFLINE_SCOPE))) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_scope, 
                    auth.getUsername(), 
                    null,
                    "This client don't have the offline access scope.");
        }
        
        // Load the refresh_token
        RefreshToken rftoken = this.refreshTokenService.findByTokenValueAndClientId(pRefreshToken, client.getId());
        
        if (rftoken == null || !Utils.isAfterNow(rftoken.getValidUntil())) {
            throw new OAuthPublicJsonException(getClass(), flow, OAuthErrorType.invalid_grant);
        }
        
        // Ok, the token is valid, so create a new access token
        return this.createTokenResponse(flow, 
                                        client.getPublicId(), 
                                        rftoken.getScopes(), 
                                        "-",
                                        pAuthentication);
        
        
    }
    
    private void validateClientCredentialRequest(String pScope,
                                                String pRedirectUri,
                                                Client pClient) {
        
        
        Flow flow = FlowFactory.clientCredential();
        
        
        
        // Required
        if (Utils.areOneEmpty(pScope, pRedirectUri)) {
            throw new OauthRestrictedException(getClass(),
                                               flow, 
                                               OAuthErrorType.invalid_request, 
                                               "-", 
                                               pRedirectUri,
                                               "One parameter is empty");
        }
        
        // Load client 
        if (pClient == null) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    "-", 
                    null,
                    "Invalid client or incorrect public id.");
        }
        
        // Check Grants
        List<ClientGrantType> grants = this.clientService.findGrantTypesByclientId(pClient.getId());
        
        if (!Utils.isOneTrue(grants, g -> g.getGrantType().getCode().equals(GrantType.CODE_CLIENT_CREDENTIALS))) {
            
            throw new OAuthPublicJsonException(getClass(),
                    flow, 
                    OAuthErrorType.unsupported_response_type);
        }
       
    }
    
    @Transactional
    public TokenResponse createTokenForClientCred(String pScope, 
                                                  String pRedirectUri,
                                                  Authentication pAuthentication) {
        
        Flow flow = FlowFactory.clientCredential();

        // Need auth
        validateAuthentication(pAuthentication, flow);
        
        SpringAuthUserDetails auth = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        Client client = this.clientService.findById(auth.getId());
        
        validateClientCredentialRequest(pScope, pRedirectUri, client);
        
        
        return this.createTokenResponse(flow, 
                                        client.getPublicId(), 
                                        pScope, 
                                        pRedirectUri,
                                        pAuthentication);
        
    }
    
    
    // Need to validate and remove the offline_access if the flow is not AUTH_CODE and if the prompt is not concent.
    @Transactional
    public TokenResponse createTokenForImplicit(Flow pFlow,
                                             String pClientPublicId,
                                             String pScopes,
                                             String pRedirectUrl, 
                                             Authentication pAuthentication) {
        
        return createTokenResponse(pFlow, pClientPublicId, pScopes, pRedirectUrl, pAuthentication);
    }
    
    TokenResponse createTokenResponse(Flow pFlow,
                                             String pClientPublicId,
                                             String pScopes,
                                             String pRedirectUrl, 
                                             Authentication pAuthentication) {
        
        validateAuthentication(pAuthentication, pFlow);

        try {
            
            SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
            
            List<Scope> scopes = this.scopeService.findScopeFromString(pScopes);
            
            TokenResponse t = new TokenResponse();
            t.setScope(pScopes);
            t.setTokenType(TokenResponse.BEARER);
            
            // Tokens
            IdToken idToken = this.idTokenService.createToken(pFlow, userDetails.getId(), pClientPublicId, scopes);
            AccessToken accessToken = this.accessTokenService.createToken(pFlow, userDetails.getId(), pClientPublicId, scopes);
            RefreshToken refreshToken = this.refreshTokenService.createToken(pFlow, userDetails.getId(), pClientPublicId, scopes);
            
            t.setIdToken(idToken);
            t.setAccessToken(accessToken);
            t.setRefreshToken(refreshToken);
            
            // Time
            long expiresIn = ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.getValidUntil());
            t.setExpiresIn(String.valueOf(expiresIn));
            
            return t;
            
        } catch (ServerException se) {
            throw se;
        } catch (Exception e) {
            throw new OauthRestrictedException(getClass(),
                                               pFlow, 
                                               OAuthErrorType.server_error, 
                                               pClientPublicId, 
                                               pRedirectUrl, 
                                               e.getMessage());
        }
        
    }
    
    private void validateAuthentication(Authentication pAuthentication, Flow pFlow) {
        
        if (pAuthentication == null || pAuthentication.getPrincipal() == null || !(pAuthentication.getPrincipal() instanceof SpringAuthUserDetails)) {
            throw new OauthRestrictedException(getClass(),
                    pFlow, 
                    OAuthErrorType.invalid_request, 
                    "-", 
                    "-",
                    "No authentication.");
        }
    }
}
