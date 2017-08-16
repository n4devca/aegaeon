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
import ca.n4dev.aegaeon.api.exception.OAuthPublicJsonException;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.AuthorizationCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.model.UserAuthorization;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.api.protocol.RequestedGrant;
import ca.n4dev.aegaeon.server.controller.dto.TokenResponse;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
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
@RequestMapping(value = AuthorizationController.URL)
public class AuthorizationController {
    
    public static final String URL = "/authorize";
    
    private UserAuthorizationService userAuthorizationService;
    private AuthorizationCodeService authorizationCodeService;
    private ClientService clientService;
    private ScopeService scopeService;
    
    private TokenServicesFacade tokenServicesFacade;
    private OpenIdEventLogger openIdEventLogger;
    
    @Autowired
    public AuthorizationController(UserAuthorizationService pUserAuthorizationService, 
                                        AuthorizationCodeService pAuthorizationCodeService,
                                        ClientService pClientService,
                                        ScopeService pScopeService,
                                        TokenServicesFacade pTokenServicesFacade,
                                        OpenIdEventLogger pOpenIdEventLogger) {
        this.userAuthorizationService = pUserAuthorizationService;
        this.authorizationCodeService = pAuthorizationCodeService;
        this.clientService = pClientService;
        this.scopeService = pScopeService;
        
        this.tokenServicesFacade = pTokenServicesFacade;
        this.openIdEventLogger = pOpenIdEventLogger;
    }
    
    @RequestMapping(value = "")
    public ModelAndView authorize(@RequestParam("response_type") String pResponseType,
                                  @RequestParam("client_id") String pClientPublicId,
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                  @RequestParam(value = "state", required = false) String pState,
                                  @RequestParam(value = "nonce", required = false) String pNonce,
                                  @RequestParam(value = "prompt", required = false) String pPrompt,
                                  @RequestParam(value = "display", required = false) String pDisplay,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {
        
        Flow flow = FlowFactory.of(pResponseType, pNonce);

        // Required
        if (Utils.areOneEmpty(pClientPublicId, pRedirectionUrl, pResponseType, pScope)) {
            throw new OauthRestrictedException(getClass(),
                                               flow, 
                                               OAuthErrorType.invalid_request, 
                                               pClientPublicId, 
                                               pRedirectionUrl,
                                               "One parameter is empty");
        }
        
        // Test method and param
        if (pRequestMethod != RequestMethod.GET && pRequestMethod != RequestMethod.POST) {
            throw new OAuthPublicRedirectionException(getClass(),
                                           flow, 
                                           OAuthErrorType.invalid_request, 
                                           pRedirectionUrl);
        }
        
        // Supported flow ?
        if (flow == null || flow.getRequestedGrant() == null || flow.getRequestedGrant().size() == 0) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectionUrl,
                    "Invalid flow");
        }
        
        // Check redirection
        Client client  = this.clientService.findByPublicId(pClientPublicId);
        if (client == null || !client.hasRedirection(pRedirectionUrl)) {
            throw new OauthRestrictedException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_request, 
                    pClientPublicId, 
                    pRedirectionUrl,
                    "Invalid redirect_uri.");
        }
        
        // Test Scopes
        List<Scope> scopes = null;
        try {
            scopes = this.scopeService.findScopeFromString(pScope);
        } catch (InvalidScopeException scex) {
            
            throw new OAuthPublicRedirectionException(getClass(),
                    flow, 
                    OAuthErrorType.invalid_scope, 
                    pRedirectionUrl);
        }
        
        Prompt p = Prompt.from(pPrompt);
        boolean isAlreadyAuthorized = isAuthorized(pAuthentication, pClientPublicId);
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
            redirect = authorizeCodeResponse(pAuthentication, flow, pClientPublicId, scopes, pRedirectionUrl, pState);
        } else if (flow.has(RequestedGrant.IMPLICIT)) {
            redirect = implicitResponse(pAuthentication, flow, pClientPublicId, scopes, pRedirectionUrl, pState);
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
        
        // Create a UserAuth and redirect
        try {
            
            SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
            
            UserAuthorization ua = this.userAuthorizationService.createUserAuthorization(userDetails.getId(), pClientPublicId, pScope);
            
            if (ua == null) {
                throw new ServerException(ServerExceptionCode.UNEXPECTED_ERROR, "Unable to create UserAuthorization.");
            }
            
            this.openIdEventLogger.log(OpenIdEvent.AUTHORIZATION, getClass(), userDetails.getUsername(), ua);
            
            return authorize(pResponseType, pClientPublicId, pScope, pRedirectionUrl, pState, null, pPrompt, pDisplay, pAuthentication, RequestMethod.POST);
            
        } catch (ServerException se) {
            // Rethrow, will be catch
            throw se;
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    
    private boolean isAuthorized(Authentication pAuthentication, String pClientPublicId) {
        Client client = this.clientService.findByPublicId(pClientPublicId);
        SpringAuthUserDetails userDetails = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        return this.userAuthorizationService.isAuthorized(userDetails.getId(), client.getId());
    }
    
    
    private RedirectView authorizeCodeResponse(Authentication pAuthentication,
                                               Flow pFlow,
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
                                          Flow pFlow,
                                          String pClientId,
                                          List<Scope> pScopes,
                                          String pRedirectionUrl,
                                          String pState) {
        
        SpringAuthUserDetails user = (SpringAuthUserDetails) pAuthentication.getPrincipal();
        
        try {
           
            TokenResponse token = this.tokenServicesFacade.createTokenResponse(pFlow, pClientId, user.getId(), pScopes, pRedirectionUrl);
            
            RedirectView view = new RedirectView(
                    UriBuilder.build(pRedirectionUrl, 
                            token,
                            pState), 
                    false);

            return view;

        } catch (Exception e) {
            // TODO(RG): Deal with RFC error
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