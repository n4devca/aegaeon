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

import ca.n4dev.aegaeon.api.exception.*;
import ca.n4dev.aegaeon.api.model.*;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * TokenServicesFacade.java
 * <p>
 * TokenServicesFacade acts as a facade for AccessTokenService and RefreshTokenService and
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
                               ClientService pClientService) {

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

        Assert.notEmpty(pCode, ServerExceptionCode.AUTH_CODE_EMPTY);

        validateAuthentication(pAuthentication);

        AuthorizationCode authCode = this.authorizationCodeService.findByCode(pCode);
        validateAuthorizationCode(authCode, pClientPublicId, pRedirectUri);

        Client client = this.clientService.findByPublicId(pClientPublicId);
        validateClient(client, pClientPublicId);
        validateRedirectionUri(client, pRedirectUri);
        validateClientFlow(client, GrantType.AUTHORIZATION_CODE);

        try {

            return createTokenResponse(new AuthRequest(authCode.getResponseType()),
                                       authCode.getClient().getPublicId(),
                                       authCode.getScopes(),
                                       authCode.getRedirectUrl(),
                                       pAuthentication);

        } finally {
            try {
                // Always delete the authorization code.
                if (authCode != null) {
                    this.authorizationCodeService.delete(authCode.getId());
                }
            } catch (Exception e) {
                LOGGER.error("Unable to delete auth code", e);
            }
        }

    }

    /**
     * TODO(RG)
     * @param pRefreshToken
     * @param pAuthentication
     * @return
     */
    @Transactional
    public TokenResponse createTokenForRefreshToken(String pRefreshToken, Authentication pAuthentication) {

        validateAuthentication(pAuthentication);
        AegaeonUserDetails auth = (AegaeonUserDetails) pAuthentication.getPrincipal();

        if (Utils.isEmpty(pRefreshToken)) {
            throw new OpenIdException(ServerExceptionCode.REFRESH_TOKEN_EMPTY, auth.getUsername(), getClass());
        }

        AuthRequest authRequest = new AuthRequest();
        Client client = this.clientService.findById(auth.getId());

        validateClient(client, auth.getUsername());

        // Check if the client has the proper scope
        validateClientScope(client, BaseTokenService.OFFLINE_SCOPE);

        // Load the refresh_token
        RefreshToken refreshToken = this.refreshTokenService.findByTokenValueAndClientId(pRefreshToken, client.getId());

        validateRefreshToken(refreshToken, client.getPublicId());

        // Ok, the token is valid, so create a new access token
        return this.createTokenResponse(authRequest,
                                        client.getPublicId(),
                                        refreshToken.getScopes(),
                                        "-",
                                        pAuthentication);


    }


    @Transactional
    public TokenResponse createTokenForClientCred(String pScopes,
                                                  String pRedirectUri,
                                                  Authentication pAuthentication) {

        // Need auth
        validateAuthentication(pAuthentication);
        AegaeonUserDetails auth = (AegaeonUserDetails) pAuthentication.getPrincipal();
        Client client = this.clientService.findById(auth.getId());

        validateClient(client, auth.getUsername());
        validateClientFlow(client, GrantType.CLIENT_CREDENTIALS);
        validateRedirectionUri(client, pRedirectUri);
        List<Scope> scopeFromString = this.scopeService.findScopeFromString(pScopes, BaseTokenService.OFFLINE_SCOPE);
        validateClientScopes(client, scopeFromString);

        return this.createTokenResponse(new AuthRequest(),
                                        client.getPublicId(),
                                        pScopes,
                                        pRedirectUri,
                                        pAuthentication);

    }

    // Need to validate and remove the offline_access if the flow is not AUTH_CODE and if the prompt is not concent.
    @Transactional
    public TokenResponse createTokenForImplicit(AuthRequest pAuthRequest,
                                                String pClientPublicId,
                                                String pScopes,
                                                String pRedirectUrl,
                                                Authentication pAuthentication) {

        validateAuthentication(pAuthentication);
        Client client = this.clientService.findByPublicId(pClientPublicId);

        validateClient(client, pClientPublicId);
        validateClientFlow(client, GrantType.IMPLICIT);
        validateClientScope(client, pScopes);
        validateRedirectionUri(client, pRedirectUrl);

        // Make sure the offline scope is not request
        List<Scope> scopeFromString = this.scopeService.findScopeFromString(pScopes, BaseTokenService.OFFLINE_SCOPE);
        validateClientScopes(client, scopeFromString);

        return createTokenResponse(pAuthRequest, pClientPublicId, pScopes, pRedirectUrl, pAuthentication);
    }

    @Transactional(readOnly = true)
    public void validateClientFlow(String pClientPublicId, GrantType pGrantType) {
        Client client = this.clientService.findByPublicId(pClientPublicId);
        validateClientFlow(client, pGrantType);
    }

    TokenResponse createTokenResponse(AuthRequest pAuthRequest,
                                      String pClientPublicId,
                                      String pScopes,
                                      String pRedirectUrl,
                                      Authentication pAuthentication) {

        validateAuthentication(pAuthentication);

        try {

            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();

            List<Scope> scopes = this.scopeService.findScopeFromString(pScopes);

            TokenResponse t = new TokenResponse();
            t.setScope(pScopes);
            t.setTokenType(TokenResponse.BEARER);

            // Tokens
            IdToken idToken = this.idTokenService.createToken(pAuthRequest, userDetails.getId(), pClientPublicId, scopes);
            AccessToken accessToken = this.accessTokenService.createToken(pAuthRequest, userDetails.getId(), pClientPublicId, scopes);
            RefreshToken refreshToken = this.refreshTokenService.createToken(pAuthRequest, userDetails.getId(), pClientPublicId, scopes);

            t.setIdToken(idToken);
            t.setAccessToken(accessToken);
            t.setRefreshToken(refreshToken);

            // Time
            long expiresIn = ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.getValidUntil());
            t.setExpiresIn(String.valueOf(expiresIn));

            return t;

        } catch (ServerException se) {
            LOGGER.warn("ServerException in " + getClass().getSimpleName() + "#createTokenResponse.", se);
            throw se;
        } catch (Exception e) {
            LOGGER.error("Exception in " + getClass().getSimpleName() + "#createTokenResponse.", e);
            throw new OpenIdException(ServerExceptionCode.UNEXPECTED_ERROR, pClientPublicId, getClass());
        }

    }

    private void validateAuthentication(Authentication pAuthentication) {

        if (pAuthentication == null || pAuthentication.getPrincipal() == null
                || !(pAuthentication.getPrincipal() instanceof AegaeonUserDetails)) {
            throw new OpenIdException(ServerExceptionCode.USER_UNAUTHENTICATED);
        }
    }

    private void validateAuthorizationCode(AuthorizationCode pAuthorizationCode,
                                           String pClientPublicId,
                                           String pRedirectUri) {

        if (pAuthorizationCode == null) {
            throw new OpenIdException(ServerExceptionCode.AUTH_CODE_EMPTY, pClientPublicId, getClass());
        }

        if (!Utils.isAfterNow(pAuthorizationCode.getValidUntil())) {
            throw new OpenIdException(ServerExceptionCode.AUTH_CODE_EXPIRED, pClientPublicId, getClass());
        }

        // Make sure, it's the same client
        if (!Utils.equals(pAuthorizationCode.getClient().getPublicId(), pClientPublicId)) {
            throw new OpenIdException(ServerExceptionCode.AUTH_CODE_UNEXPECTED_CLIENT, pClientPublicId, getClass());
        }

        // Make sure the redirection is the same than previously
        if (!Utils.equals(pAuthorizationCode.getRedirectUrl(), pRedirectUri)) {
            throw new OpenIdException(ServerExceptionCode.AUTH_CODE_UNEXPECTED_REDIRECTION, pClientPublicId, getClass());
        }
    }

    private void validateRefreshToken(RefreshToken pRefreshToken, String pClientPublicId) {
        if (pRefreshToken == null) {
            throw new OpenIdException(ServerExceptionCode.REFRESH_TOKEN_EMPTY, pClientPublicId, getClass());
        }

        if (!Utils.isAfterNow(pRefreshToken.getValidUntil())) {
            throw new OpenIdException(ServerExceptionCode.REFRESH_TOKEN_EXPIRED, pClientPublicId, getClass());
        }
    }

    private void validateClient(Client pClient, String pClientPublicId) {

        if (pClient == null || Utils.isEmpty(pClient.getPublicId()) || Utils.isEmpty(pClientPublicId)) {
            throw new OpenIdException(ServerExceptionCode.CLIENT_EMPTY, pClientPublicId, getClass());
        }
    }

    private void validateClientFlow(Client pClient, GrantType pGrantType) {

        boolean fail = true;
        Long id = pClient != null ? pClient.getId() : null;
        String publicId = pClient != null ? pClient.getPublicId() : null;

        if (pGrantType != null && id != null) {
            // Did we set this client to use this flow
            List<ClientAuthFlow> grants = this.clientService.findAuthFlowByclientId(id);

            fail = !Utils.isOneTrue(grants, g -> g.getFlow().equals(pGrantType));
        }

        if (fail) {
            throw new OpenIdException(ServerExceptionCode.CLIENT_UNAUTHORIZED_FLOW, publicId, getClass());
        }
    }

    private void validateRedirectionUri(Client pClient, String pRedirectUri) {

        boolean fail = true;
        Long id = pClient != null ? pClient.getId() : null;
        String publicId = pClient != null ? pClient.getPublicId() : null;

        if (Utils.isNotEmpty(pRedirectUri) && id != null) {
            List<ClientRedirection> redirectionList = this.clientService.findRedirectionsByclientId(id);
            fail = redirectionList == null || !Utils.isOneTrue(redirectionList, cr -> cr.getUrl().equals(pRedirectUri));
        }

        if (fail) {
            throw new OpenIdException(ServerExceptionCode.CLIENT_REDIRECTIONURL_INVALID, publicId, getClass());
        }
    }

    private void validateClientScopes(Client pClient, List<Scope> pScopes) {
        if (Utils.isNotEmpty(pScopes)) {
            pScopes.forEach(pScope -> validateClientScope(pClient, pScope.getName()));
        }
    }

    private void validateClientScope(Client pClient, String pScope) {
        Long id = pClient != null ? pClient.getId() : null;
        String publicId = pClient != null ? pClient.getPublicId() : null;
        boolean fail = true;

        if (id != null && Utils.isNotEmpty(pScope)) {
            List<ClientScope> clientScopes = this.clientService.findScopeByClientId(id);
            fail = !Utils.isOneTrue(clientScopes, cs -> cs.getScope().getName().equals(pScope));
        }

        if (fail) {
            throw new OpenIdException(ServerExceptionCode.CLIENT_UNAUTHORIZED_SCOPE, publicId, getClass());
        }
    }

}
