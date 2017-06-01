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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.api.exception.InvalidScopeException;
import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.protocol.AuthorizationGrant;
import ca.n4dev.aegaeon.server.controller.dto.TokenResponse;
import ca.n4dev.aegaeon.server.model.AccessToken;
import ca.n4dev.aegaeon.server.model.AuthorizationCode;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.model.UserAuthorization;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.AccessTokenService;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * OAuthAuthorizationController.java
 * 
 * Controller used to either return an access token (implicit) or 
 * an authorize code.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Controller
@RequestMapping(value = OAuthAuthorizationController.URL)
public class OAuthAuthorizationController {
    
    public static final String URL = "/authorize";
    
    private UserAuthorizationService userAuthorizationService;
    private AuthorizationCodeService authorizationCodeService;
    private AccessTokenService accessTokenService;
    private ClientService clientService;
    private ScopeService scopeService;
    
    @Autowired
    public OAuthAuthorizationController(UserAuthorizationService pUserAuthorizationService, 
                                        AuthorizationCodeService pAuthorizationCodeService,
                                        AccessTokenService pAccessTokenService,
                                        ClientService pClientService,
                                        ScopeService pScopeService) {
        this.userAuthorizationService = pUserAuthorizationService;
        this.authorizationCodeService = pAuthorizationCodeService;
        this.accessTokenService = pAccessTokenService;
        this.clientService = pClientService;
        this.scopeService = pScopeService;
    }
    
    @RequestMapping(value = "")
    public ModelAndView authorize(@RequestParam("response_type") String pResponseType,
                                  @RequestParam("client_id") String pClientPublicId,
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                  @RequestParam(value = "state", required = false) String pState,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {
        
        // Required
        if (Utils.areOneEmpty(pClientPublicId, pRedirectionUrl, pResponseType, pScope)) {
            throw new OauthRestrictedException(AuthorizationGrant.from(pResponseType), 
                                               OAuthErrorType.invalid_request, 
                                               pClientPublicId, 
                                               pRedirectionUrl,
                                               "One parameter is empty");
        }
        
        // Test method and param
        if (pRequestMethod != RequestMethod.GET && pRequestMethod != RequestMethod.POST) {
            throw new OAuthPublicRedirectionException(AuthorizationGrant.from(pResponseType), 
                                           OAuthErrorType.invalid_request, 
                                           pRedirectionUrl);
        }
        
        // Test Scopes
        List<Scope> scopes = null;
        try {
            scopes = this.scopeService.findScopeFromString(pScope);
        } catch (InvalidScopeException scex) {
            
            throw new OAuthPublicRedirectionException(AuthorizationGrant.from(pResponseType), 
                    OAuthErrorType.invalid_scope, 
                    pRedirectionUrl);
        }
        
        if (!isAuthorize(pAuthentication, pClientPublicId)) {
            ModelAndView authPage = new ModelAndView("authorize");
            
            authPage.addObject("client_id", pClientPublicId);
            authPage.addObject("redirection_url", pRedirectionUrl);
            authPage.addObject("scopes", pScope);
            authPage.addObject("state", pState);
            authPage.addObject("response_type", pResponseType);
            
            
            return authPage;
        }
        
        AuthorizationGrant granType = AuthorizationGrant.from(pResponseType);
        RedirectView redirect = null;
        
        // TODO(RG): Client Credential
        if (granType == AuthorizationGrant.AUTHORIZATIONCODE) {
            redirect = authorizeCodeResponse(pAuthentication, pResponseType, pClientPublicId, scopes, pRedirectionUrl, pState);
        } else if (granType == AuthorizationGrant.IMPLICIT) {
            redirect = implicitResponse(pAuthentication, pResponseType, pClientPublicId, scopes, pRedirectionUrl, pState);
        } 
        
        return new ModelAndView(redirect);
    }
    
    @RequestMapping(value = "/accept")
    public ModelAndView addUserAuthorization(@RequestParam("response_type") String pResponseType,
                                             @RequestParam("client_id") String pClientPublicId,
                                             @RequestParam(value = "scope", required = false) String pScope,
                                             @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                             @RequestParam(value = "state", required = false) String pState,
                                             Authentication pAuthentication) {
        
        // Create a UserAuth and redirect
        try {
            Client client = this.clientService.findByPublicId(pClientPublicId);
            SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
            
            UserAuthorization ua = this.userAuthorizationService.save(new UserAuthorization(userDetails.getId(), client.getId(), pScope));
            
            if (ua == null) {
                throw new RuntimeException("Unable to create ua.");
            }
            
            return authorize(pResponseType, pClientPublicId, pScope, pRedirectionUrl, pState, pAuthentication, RequestMethod.POST);
            
        } catch (ServerException se) {
            // Rethrow, will be catch
            throw se;
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    
    private boolean isAuthorize(Authentication pAuthentication, String pClientPublicId) {
        Client client = this.clientService.findByPublicId(pClientPublicId);
        SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        return this.userAuthorizationService.findByUserIdAndClientId(userDetails.getId(), client.getId()) != null;
    }
    
    
    private RedirectView authorizeCodeResponse(Authentication pAuthentication,
                                               String pResponseType,
                                               String pClientId,
                                               List<Scope> pScopes,
                                               String pRedirectionUrl,
                                               String pState) {

        
        try {
            
            // Create auth code
            SpringAuthUserDetails user = (SpringAuthUserDetails) pAuthentication.getPrincipal();
            AuthorizationCode code = this.authorizationCodeService.createCode(user.getId(), pClientId, pScopes, pRedirectionUrl);

            // Returned values
            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
            params.add("state", pState);
            params.add("code", code.getCode());
            
            String url = UriBuilder.build(pRedirectionUrl, params);
            RedirectView view = new RedirectView(url, false);
            
            return view;
            
        } catch (ServerException se) {
            // Rethrow, will be catch
            throw se;    
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    
    private RedirectView implicitResponse(Authentication pAuthentication, 
                                          String pResponseType,
                                          String pClientId,
                                          List<Scope> pScopes,
                                          String pRedirectionUrl,
                                          String pState) {
        
        SpringAuthUserDetails user = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        try {
            
            // Create a token
            AccessToken accessToken = this.accessTokenService.createAccessToken(user.getId(), pClientId, pScopes);
            
            long expiresIn = ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.getValidUntil());
            
            RedirectView view = new RedirectView(
                    UriBuilder.build(pRedirectionUrl, 
                            TokenResponse.bearer(accessToken.getToken(), String.valueOf(expiresIn), accessToken.getScopes()),
                            pState), 
                    false);

            return view;

        } catch (Exception e) {
            // TODO(RG): Deal with RFC error
            throw new ServerException(e);
        }
        
    }
}
