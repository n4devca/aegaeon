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
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.server.controller.dto.TokenResponse;
import ca.n4dev.aegaeon.server.exception.ServerException;
import ca.n4dev.aegaeon.server.model.AccessToken;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.UserAuthorization;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.AccessTokenService;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import ca.n4dev.aegaeon.server.utils.UriBuilder;

/**
 * OAuthAuthorizationController.java
 * 
 * TODO(rguillemette) Add description
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
    
    
    @Autowired
    public OAuthAuthorizationController(UserAuthorizationService pUserAuthorizationService, 
                                        AuthorizationCodeService pAuthorizationCodeService,
                                        AccessTokenService pAccessTokenService,
                                        ClientService pClientService) {
        this.userAuthorizationService = pUserAuthorizationService;
        this.authorizationCodeService = pAuthorizationCodeService;
        this.accessTokenService = pAccessTokenService;
        this.clientService = pClientService;
    }
    
    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView authorize(@RequestParam("response_type") String pResponseType,
                                  @RequestParam("client_id") String pClientPublicId,
                                  @RequestParam(value = "scope", required = false) String[] pScope,
                                  @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                  @RequestParam(value = "state", required = false) String pState,
                                  Authentication pAuthentication) {
        
        if (!isAuthorize(pAuthentication, pClientPublicId)) {
            ModelAndView authPage = new ModelAndView("authorize");
            
            authPage.addObject("client_id", pClientPublicId);
            authPage.addObject("redirection_url", pRedirectionUrl);
            authPage.addObject("scope", pScope);
            authPage.addObject("state", pState);
            authPage.addObject("response_type", pResponseType);
            
            
            return authPage;
        }
        
        // We are authorize, so redirect
        return new ModelAndView(response(pAuthentication, pResponseType, pClientPublicId, pScope, pRedirectionUrl, pState));
    }
    
    @RequestMapping(value = "/accept")
    public ModelAndView addUserAuthorization(@RequestParam("response_type") String pResponseType,
                                             @RequestParam("client_id") String pClientPublicId,
                                             @RequestParam(value = "scope", required = false) String[] pScope,
                                             @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                             @RequestParam(value = "state", required = false) String pState,
                                             Authentication pAuthentication) {
        
        // Create a UserAuth and redirect
        Client client = this.clientService.findByPublicId(pClientPublicId);
        SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        UserAuthorization ua = this.userAuthorizationService.save(new UserAuthorization(userDetails.getId(), client.getId()));
        
        if (ua == null) {
            throw new RuntimeException("Unable to create ua.");
        }
        
        return authorize(pResponseType, pClientPublicId, pScope, pRedirectionUrl, pState, pAuthentication);
        
    }
    
    private boolean isAuthorize(Authentication pAuthentication, String pClientPublicId) {
        Client client = this.clientService.findByPublicId(pClientPublicId);
        SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        return this.userAuthorizationService.findByUserIdAndClientId(userDetails.getId(), client.getId()) != null;
    }
    
    private RedirectView response(Authentication pAuthentication,
                                  String pResponseType,
                                  String pClientId,
                                  String[] pScope,
                                  String pRedirectionUrl,
                                  String pState) {
        
        if ("code".equalsIgnoreCase(pResponseType)) {
            return authorizeCodeResponse(pAuthentication, pResponseType, pClientId, pScope, pRedirectionUrl, pState);
        } else { // if
            return implicitResponse(pAuthentication, pResponseType, pClientId, pScope, pRedirectionUrl, pState);
        }
    }
    
    private RedirectView authorizeCodeResponse(Authentication pAuthentication,
                                               String pResponseType,
                                               String pClientId,
                                               String[] pScope,
                                               String pRedirectionUrl,
                                               String pState) {

        SpringAuthUserDetails user = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        try {
            
            RedirectView view = new RedirectView(pRedirectionUrl, false);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
        
        
        return null;
    }
    
    private RedirectView implicitResponse(Authentication pAuthentication, 
                                          String pResponseType,
                                          String pClientId,
                                          String[] pScope,
                                          String pRedirectionUrl,
                                          String pState) {
        
        SpringAuthUserDetails user = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        try {
            
            // Create a token
            AccessToken accessToken = this.accessTokenService.createAccessToken(user.getId(), pClientId);
            
            long expiresIn = ChronoUnit.SECONDS.between(accessToken.getValidUntil(), LocalDateTime.now());
            
            RedirectView view = new RedirectView(
                    UriBuilder.build(pRedirectionUrl, 
                            TokenResponse.bearer(accessToken.getToken(), String.valueOf(expiresIn), Arrays.asList("openid"))), 
                    false);

            return view;

        } catch (Exception e) {
            // TODO(RG): Deal with RFC error
            throw new ServerException(e.getMessage());
        }
        
    }
}
