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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.OpenIdException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.AuthorizationCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientAuthFlow;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.ClientRequest;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.server.controller.exception.InvalidAuthorizationCodeException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidClientIdException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidClientRedirectionException;
import ca.n4dev.aegaeon.server.controller.exception.InvalidRefreshTokenException;
import ca.n4dev.aegaeon.server.controller.exception.UnauthorizedClient;
import ca.n4dev.aegaeon.server.controller.exception.UnauthorizedGrant;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import ca.n4dev.aegaeon.server.view.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TokenResponse createTokenForAuthCode(TokenRequest pTokenRequest,
                                                Authentication pAuthentication) {


        validateAuthentication(pAuthentication);

        Client client = validateClientAnReturn(pTokenRequest, Flow.authorization_code);
        AuthorizationCode authCode = this.authorizationCodeService.findByCode(pTokenRequest.getCode());
        validateAuthorizationCode(pTokenRequest, authCode);

        try {

//            final AuthRequest authRequest =
//                    new AuthRequest(authCode.getResponseType(), authCode.getScopes(), pTokenRequest.getClientId(),
//                                    authCode.getRedirectUrl(),
//                                    null, null, null, null, null, null);

            // Complete info
            pTokenRequest.setResponseType(authCode.getResponseType());
            pTokenRequest.setScope(authCode.getScopes());
            pTokenRequest.setNonce(authCode.getNonce());

            final Authentication codeAuthentication = createAuthenticationFromAuthCode(pTokenRequest, authCode);
            return createTokenResponse(pTokenRequest,
                                       codeAuthentication);

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
     * @param pTokenRequest
     * @param pAuthentication
     * @return
     */
    @Transactional
    public TokenResponse createTokenForRefreshToken(TokenRequest pTokenRequest, Authentication pAuthentication) {

        validateAuthentication(pAuthentication);
        AegaeonUserDetails auth = (AegaeonUserDetails) pAuthentication.getPrincipal();

        if (Utils.isEmpty(pTokenRequest.getRefreshToken())) {
            throw new OpenIdException(ServerExceptionCode.REFRESH_TOKEN_EMPTY, auth.getUsername(), getClass());
        }

        Client client = validateClientAnReturn(pTokenRequest, Flow.authorization_code);

        // Need to be equals
        Assert.isTrue(Utils.equals(client.getId(), auth.getId()),
                      () -> new UnauthorizedClient(pTokenRequest));

        // Check if the client has the proper scope
        validateClientScope(client, BaseTokenService.OFFLINE_SCOPE);

        // Load the refresh_token
        RefreshToken refreshToken = this.refreshTokenService.findByTokenValueAndClientId(pTokenRequest.getRefreshToken(), client.getId());
        validateRefreshToken(pTokenRequest, refreshToken);

        // Validate scope, otherwise reuse refresh_token scopes
        boolean validRequestedScopes = false;
        if (Utils.isNotEmpty(pTokenRequest.getScope())) {
            validRequestedScopes = scopeService.isPartOf(refreshToken.getScopes(), pTokenRequest.getScope());
        }

        if (!validRequestedScopes) {
            pTokenRequest.setScope(refreshToken.getScopes());
        }

        // In any case, remove the openid scope because we can't meet the check requirements right now
        pTokenRequest.setScope(withoutOpenIdScope(pTokenRequest.getScope()));

        // Ok, the token is valid, so create a new access token
        return this.createTokenResponse(pTokenRequest,
                                        pAuthentication);


    }


    @Transactional
    public TokenResponse createTokenForClientCred(TokenRequest pTokenRequest,
                                                  Authentication pAuthentication) {

        // Need auth
        validateAuthentication(pAuthentication);
        AegaeonUserDetails auth = (AegaeonUserDetails) pAuthentication.getPrincipal();
        final Client client = validateClientAnReturn(pTokenRequest, Flow.client_credentials);
        validateClient(client, auth.getUsername());

        Set<ScopeView> scopes = this.scopeService.getValidScopes(pTokenRequest.getScope(), Utils.asSet(BaseTokenService.OFFLINE_SCOPE));
        validateClientScopes(client, scopes);

        return this.createTokenResponse(pTokenRequest,
                                        pAuthentication);

    }

    // Need to validate and remove the offline_access if the flow is not AUTH_CODE and if the prompt is not concent.
    @Transactional
    public TokenResponse createTokenForImplicit(AuthRequest pAuthRequest,
                                                Authentication pAuthentication) {

        validateAuthentication(pAuthentication);
        Client client = validateClientAnReturn(pAuthRequest, Flow.implicit);
        //validateClientFlow(client, GrantType.IMPLICIT);
        //validateRedirectionUri(client, pAuthRequest.getRedirectUri());

        // Make sure the offline scope is not request
        Set<ScopeView> scopes = this.scopeService.getValidScopes(pAuthRequest.getScope(), Utils.asSet(BaseTokenService.OFFLINE_SCOPE));
        validateClientScopes(client, scopes);

        return createTokenResponse(new TokenRequest(pAuthRequest), pAuthentication);
    }

    TokenResponse createTokenResponse(TokenRequest pTokenRequest,
                                      Authentication pAuthentication) {

        validateAuthentication(pAuthentication);

        try {

            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();

            Set<ScopeView> scopes = this.scopeService.getValidScopes(pTokenRequest.getScope());

            TokenResponse t = new TokenResponse();
            t.setScope(pTokenRequest.getScope());
            t.setTokenType(TokenResponse.BEARER);

            // Tokens
            IdToken idToken = this.idTokenService.createToken(pTokenRequest, userDetails.getId(), pTokenRequest.getClientId(), scopes);
            AccessToken accessToken =
                    this.accessTokenService.createToken(pTokenRequest, userDetails.getId(), pTokenRequest.getClientId(), scopes);
            RefreshToken refreshToken =
                    this.refreshTokenService.createToken(pTokenRequest, userDetails.getId(), pTokenRequest.getClientId(), scopes);

            t.setIdToken(idToken);
            t.setAccessToken(accessToken);
            t.setRefreshToken(refreshToken);

            // Time
            long expiresIn = ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.getValidUntil());
            t.setExpiresIn(expiresIn);

            return t;

        } catch (ServerException se) {
            LOGGER.warn("ServerException in " + getClass().getSimpleName() + "#createTokenResponse.", se);
            throw se;
        } catch (Exception e) {
            LOGGER.error("Exception in " + getClass().getSimpleName() + "#createTokenResponse.", e);
            throw new OpenIdException(ServerExceptionCode.UNEXPECTED_ERROR, pTokenRequest.getClientId(), getClass());
        }

    }

    private void validateAuthentication(Authentication pAuthentication) {

        if (pAuthentication == null || pAuthentication.getPrincipal() == null
                || !(pAuthentication.getPrincipal() instanceof AegaeonUserDetails)) {
            throw new OpenIdException(ServerExceptionCode.USER_UNAUTHENTICATED);
        }
    }

    private void validateAuthorizationCode(TokenRequest pTokenRequest,
                                           AuthorizationCode pAuthorizationCode) {

        Assert.notNull(pAuthorizationCode,
                       () -> new InvalidAuthorizationCodeException(pTokenRequest, InvalidAuthorizationCodeException.EMPTY));

        Assert.isTrue(Utils.isAfterNow(pAuthorizationCode.getValidUntil()),
                      () -> new InvalidAuthorizationCodeException(pTokenRequest, InvalidAuthorizationCodeException.EXPIRED));

        // Make sure, it's the same client
        Assert.equals(pAuthorizationCode.getClient().getPublicId(), pTokenRequest.getClientId(),
                      () -> new InvalidAuthorizationCodeException(pTokenRequest, InvalidAuthorizationCodeException.INVALID_CLIENT));

        // Make sure the redirection is the same than previously
        Assert.equals(pAuthorizationCode.getRedirectUrl(), pTokenRequest.getRedirectUri(),
                      () -> new InvalidAuthorizationCodeException(pTokenRequest, InvalidAuthorizationCodeException.INVALID_CLIENT_URI));


    }

    private void validateRefreshToken(TokenRequest pTokenRequest, RefreshToken pRefreshToken) {

        Assert.notNull(pRefreshToken, () -> new InvalidRefreshTokenException(pTokenRequest, InvalidRefreshTokenException.EMPTY));

        Assert.isTrue(Utils.isAfterNow(pRefreshToken.getValidUntil()),
                      () -> new InvalidRefreshTokenException(pTokenRequest, InvalidRefreshTokenException.EXPIRED));

        Assert.isTrue(Utils.equals(pTokenRequest.getClientId(), pRefreshToken.getClient().getPublicId()),
                      () -> new InvalidRefreshTokenException(pTokenRequest, InvalidRefreshTokenException.INVALID_CLIENT));
    }

    private Client validateClientAnReturn(ClientRequest pClientRequest, Flow pFlow) {

        final String clientId = pClientRequest.getClientId();
        final String redirectUri = pClientRequest.getRedirectUri();

        Assert.notEmpty(clientId, () -> new InvalidClientIdException(pClientRequest));
        Assert.notEmpty(redirectUri, () -> new InvalidClientRedirectionException(pClientRequest));

        Client client = this.clientService.findByPublicId(pClientRequest.getClientId());
        validateClient(client, pClientRequest.getClientId());

        validateRedirectionUri(pClientRequest, client, pClientRequest.getRedirectUri());
        validateClientFlow(pClientRequest, client, pFlow);

        return client;
    }


    private void validateClient(Client pClient, String pClientPublicId) {

        if (pClient == null || Utils.isEmpty(pClient.getPublicId()) || Utils.isEmpty(pClientPublicId)
                || !Utils.equals(pClientPublicId, pClient.getPublicId())) {
            throw new OpenIdException(ServerExceptionCode.CLIENT_EMPTY, pClientPublicId, getClass());
        }
    }

    @Transactional(readOnly = true)
    public void validateClientFlow(ClientRequest pClientRequest, String pClientId, Flow pFlow) {
        validateClientFlow(pClientRequest, clientService.findByPublicId(pClientId), pFlow);
    }

    private void validateClientFlow(ClientRequest pClientRequest, Client pClient, Flow pFlow) {

        boolean fail = true;
        Long id = pClient != null ? pClient.getId() : null;

        if (pFlow != null && id != null) {
            // Did we set this client to use this flow
            List<ClientAuthFlow> grants = this.clientService.findAuthFlowByClientId(id);
            fail = !Utils.isOneTrue(grants, g -> g.getFlow().equals(pFlow));
        }

        if (fail) {
            throw new UnauthorizedGrant(pClientRequest);
        }
    }


    private void validateRedirectionUri(ClientRequest pClientRequest, Client pClient, String pRedirectUri) {

        boolean fail = true;
        Long id = pClient != null ? pClient.getId() : null;
        String publicId = pClient != null ? pClient.getPublicId() : null;

        if (Utils.isNotEmpty(pRedirectUri) && id != null) {
            List<ClientRedirection> redirectionList = this.clientService.findRedirectionsByClientId(id);
            fail = redirectionList == null || !Utils.isOneTrue(redirectionList, cr -> cr.getUrl().equals(pRedirectUri));
        }

        if (fail) {
            throw new InvalidClientRedirectionException(pClientRequest);
        }
    }

    private void validateClientScopes(Client pClient, Set<ScopeView> pScopes) {
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

    private Authentication createAuthenticationFromAuthCode(TokenRequest pTokenRequest, AuthorizationCode pAuthorizationCode) {
        Assert.notNull(pAuthorizationCode,
                       () -> new InvalidAuthorizationCodeException(pTokenRequest, InvalidAuthorizationCodeException.EMPTY));

        Assert.notNull(pAuthorizationCode.getUser(),
                       () -> new InvalidAuthorizationCodeException(pTokenRequest, InvalidAuthorizationCodeException.EMPTY_USER));


        final User user = pAuthorizationCode.getUser();

        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        user.getAuthorities().forEach(a -> auths.add(new SimpleGrantedAuthority(a.getCode() /*a.getCode().replace("ROLE_", "")*/)));
        final AegaeonUserDetails aegaeonUserDetails =
                new AegaeonUserDetails(user.getId(), user.getUserName(), user.getPasswd(), user.isEnabled(), true, auths);

        return new CodeAuthentication(aegaeonUserDetails);
    }

    private String withoutOpenIdScope(String pScopes) {
        if (Utils.isNotEmpty(pScopes)) {

            String scopeNoOpenId = pScopes.replace("openid", "").trim();
            return scopeNoOpenId;
        }

        return pScopes;
    }

    private static final class CodeAuthentication extends AbstractAuthenticationToken {

        private UserDetails userDetails;

        public CodeAuthentication(AegaeonUserDetails pUserDetails) {
            super(null);
            userDetails = pUserDetails;
        }

        @Override
        public Object getPrincipal() {
            return userDetails;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

    }
}
