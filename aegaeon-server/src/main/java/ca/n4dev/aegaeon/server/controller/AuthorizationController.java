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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicJsonException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.api.protocol.RequestedGrant;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.AuthorizationService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.view.AuthorizationCodeView;
import ca.n4dev.aegaeon.server.view.TokenResponse;

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
@RequestMapping(value = AuthorizationController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "oauth", havingValue = "true", matchIfMissing = true)
public class AuthorizationController {
    
    public static final String URL = "/authorize";
    
    private UserAuthorizationService userAuthorizationService;
    private AuthorizationCodeService authorizationCodeService;
    private AuthorizationService authorizationService;
    private TokenServicesFacade tokenServicesFacade;
    
    @Autowired
    public AuthorizationController(AuthorizationService pAuthorizationService,
                                   UserAuthorizationService pUserAuthorizationService, 
                                   AuthorizationCodeService pAuthorizationCodeService,
                                   TokenServicesFacade pTokenServicesFacade) {
        
        this.authorizationService = pAuthorizationService;
        
        this.userAuthorizationService = pUserAuthorizationService;
        this.authorizationCodeService = pAuthorizationCodeService;
        
        this.tokenServicesFacade = pTokenServicesFacade;
    }
    
    @RequestMapping(value = "")
    public ModelAndView authorize(@RequestParam(value = "response_type", required = false) String pResponseType,
                                  @RequestParam(value = "client_id", required = false) String pClientPublicId,
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                  @RequestParam(value = "state", required = false) String pState,
                                  @RequestParam(value = "nonce", required = false) String pNonce,
                                  @RequestParam(value = "prompt", required = false) String pPrompt,
                                  @RequestParam(value = "display", required = false) String pDisplay,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {
        
        Flow flow = FlowFactory.of(pResponseType, pNonce);
        Prompt p = Prompt.from(pPrompt);
        
        
        // Validate request: No exception == OK
        this.authorizationService.validateAuthorizationRequest(pResponseType, pRequestMethod, flow, pClientPublicId, pRedirectionUrl, pScope);
        
        boolean isAlreadyAuthorized = this.authorizationService.isAuthorized(pAuthentication, pClientPublicId);
        
        ModelAndView authPage = authorizationPage(pResponseType, pClientPublicId, pRedirectionUrl, pScope, pState, pPrompt, pDisplay);
        
        
        if (p != null) {
            
            if (p == Prompt.none && !isAlreadyAuthorized) {
                throw new OAuthPublicJsonException(getClass(), flow, OAuthErrorType.unauthorized_client);
            } else if (!isAlreadyAuthorized || p == Prompt.login || p == Prompt.consent) {
                return authPage;
            }  // else OK
            
        } else if (!isAlreadyAuthorized) {
            return authPage;
        }
        
        RedirectView redirect = null;
        
        // TODO(RG): Client Credential
        if (flow.has(RequestedGrant.AUTHORIZATIONCODE)) {
            redirect = authorizeCodeResponse(pAuthentication, flow, pClientPublicId, pScope, pRedirectionUrl, pState);
        } else if (flow.has(RequestedGrant.IMPLICIT)) {
            redirect = implicitResponse(pAuthentication, flow, pClientPublicId, pScope, pRedirectionUrl, pState);
        } 
        
        return new ModelAndView(redirect);
    }
    
    @RequestMapping(value = "/accept")
    public ModelAndView addUserAuthorization(@RequestParam("response_type") String pResponseType,
                                             @RequestParam("client_id") String pClientPublicId,
                                             @RequestParam(value = "scope", required = false) String pScope,
                                             @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                             @RequestParam(value = "state", required = false) String pState,
                                             @RequestParam(value = "prompt", required = false) String pPrompt,
                                             @RequestParam(value = "display", required = false) String pDisplay,
                                             Authentication pAuthentication) {
        
        try {
            
            // Create a UserAuth and redirect
            SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
            this.userAuthorizationService.createOneUserAuthorization(userDetails, pClientPublicId, pScope);
            
            return authorize(pResponseType, pClientPublicId, pScope, pRedirectionUrl, pState, null, pPrompt, pDisplay, pAuthentication, RequestMethod.POST);
            
        } catch (ServerException se) {
            // Rethrow, will be catch
            throw se;
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    
    private RedirectView authorizeCodeResponse(Authentication pAuthentication,
                                               Flow pFlow,
                                               String pClientId,
                                               String pScopes,
                                               String pRedirectionUrl,
                                               String pState) {

        
        try {
            
            // Create auth code
            SpringAuthUserDetails user = (SpringAuthUserDetails) pAuthentication.getPrincipal();
            AuthorizationCodeView code = this.authorizationCodeService.createCode(user.getId(), pClientId, pScopes, pRedirectionUrl);

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
                                          Flow pFlow,
                                          String pClientId,
                                          String pScopes,
                                          String pRedirectionUrl,
                                          String pState) {
        
        
        try {
           
            TokenResponse token = this.tokenServicesFacade.createTokenForImplicit(pFlow, pClientId, pScopes, pRedirectionUrl, pAuthentication);
            
            RedirectView view = new RedirectView(
                    UriBuilder.build(pRedirectionUrl, 
                            token,
                            pState), 
                    false);

            return view;
        } catch (ServerException se) {
            throw se;
        } catch (Exception e) {
            throw new ServerException(e);
        }
        
    }
    
    /**
     * Create a page to ask user consent.
     * @see authorize endpoint.
     * 
     * @param pResponseType The response type.
     * @param pClientPublicId The client public id.
     * @param pRedirectionUrl The selected redirect url
     * @param pScope The requested scopes.
     * @param pState A client state.
     * @param pPrompt Which prompt option.
     * @param pDisplay How to display page.
     * @return A model and view.
     */
    private ModelAndView authorizationPage(String pResponseType, 
                                           String pClientPublicId, 
                                           String pRedirectionUrl, 
                                           String pScope, 
                                           String pState, 
                                           String pPrompt, 
                                           String pDisplay) {
        
        ModelAndView authPage = new ModelAndView("authorize");
        
        authPage.addObject("client_id", pClientPublicId);
        authPage.addObject("redirection_url", pRedirectionUrl);
        authPage.addObject("scopes", pScope);
        authPage.addObject("state", pState);
        authPage.addObject("response_type", pResponseType);
        authPage.addObject("display", pDisplay);
        authPage.addObject("prompt", pPrompt);
        
        
        return authPage;
    }
}
