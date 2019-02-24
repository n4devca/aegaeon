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

import ca.n4dev.aegaeon.api.exception.ErrorHandling;
import ca.n4dev.aegaeon.api.exception.OpenIdException;
import ca.n4dev.aegaeon.api.exception.OpenIdExceptionBuilder;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.AuthorizationService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.AuthorizationCodeView;
import ca.n4dev.aegaeon.server.view.TokenResponse;
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

    /*
    *
    * */
    @RequestMapping(value = "")
    public ModelAndView authorize(
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "response_type", required = false) String pResponseType,
                                  @RequestParam(value = "client_id", required = false) String pClientPublicId,
                                  @RequestParam(value = "redirect_uri", required = false) String pRedirectUri,
                                  @RequestParam(value = "state", required = false) String pState,
                                  @RequestParam(value = "nonce", required = false) String pNonce,
                                  @RequestParam(value = "display", required = false) String pDisplay,
                                  @RequestParam(value = "prompt", required = false) String pPrompt,
                                  @RequestParam(value = "id_token_hint", required = false) String pIdTokenHint,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {


        GrantType grantType = null;
        RedirectView redirect = null;
        Prompt p = Prompt.from(pPrompt);
        AuthRequest authRequest = new AuthRequest(pResponseType, pNonce, pState);

        try {
            // Validate basic info from request
            Assert.notEmpty(pClientPublicId, ServerExceptionCode.CLIENT_EMPTY);
            Assert.notEmpty(pRedirectUri, ServerExceptionCode.CLIENT_REDIRECTURL_EMPTY);

            // Make sure the client and redirection is valid
            if (!authorizationService.isClientInfoValid(pClientPublicId, pRedirectUri)) {
                Utils.raise(ServerExceptionCode.CLIENT_REDIRECTIONURL_INVALID);
            }

            Assert.notEmpty(pScope, ServerExceptionCode.SCOPE_EMPTY);
            Assert.notEmpty(pResponseType, ServerExceptionCode.CLIENT_UNAUTHORIZED_FLOW);
            Assert.isTrue(pRequestMethod == RequestMethod.GET
                                  || pRequestMethod == RequestMethod.POST,
                          ServerExceptionCode.REQUEST_TYPE_INVALID);

            boolean isAlreadyAuthorized = this.authorizationService.isAuthorized(pAuthentication, pClientPublicId);

            ModelAndView authPage = authorizationPage(pResponseType, pClientPublicId, pRedirectUri, pScope, pState, pPrompt, pDisplay);


            if (p != null) {

                if (p == Prompt.none && !isAlreadyAuthorized) {
                    throw new OpenIdException(ServerExceptionCode.USER_UNAUTHENTICATED);
                } else if (!isAlreadyAuthorized || p == Prompt.login || p == Prompt.consent) {
                    return authPage;
                }  // else OK

            } else if (!isAlreadyAuthorized) {
                return authPage;
            }

            // TODO(RG): Client Credential
            grantType = FlowUtils.getAuthorizationType(authRequest);

            if (grantType == GrantType.AUTHORIZATION_CODE) {
                redirect = authorizeCodeResponse(pAuthentication, authRequest, pClientPublicId, pScope, pRedirectUri, pState);
            } else if (grantType == GrantType.IMPLICIT) {
                redirect = implicitResponse(pAuthentication, authRequest, pClientPublicId, pScope, pRedirectUri, pState);
            } else {
                Utils.raise(ServerExceptionCode.RESPONSETYPE_INVALID);
            }

            return new ModelAndView(redirect);

        } catch (Exception pException) {
            // Add info and rethrow
            throw new OpenIdExceptionBuilder(pException)
                    .clientId(pClientPublicId)
                    .redirection(pRedirectUri)
                    .state(pState)
                    .handling(ErrorHandling.REDIRECT)
                    .from(grantType).build();
        }

    }



    @RequestMapping(value = "/accept")
    public ModelAndView addUserAuthorization(@RequestParam("response_type") String pResponseType,
                                             @RequestParam("client_id") String pClientPublicId,
                                             @RequestParam(value = "scope", required = false) String pScope,
                                             @RequestParam(value = "redirection_url", required = false) String pRedirectionUrl,
                                             @RequestParam(value = "state", required = false) String pState,
                                             @RequestParam(value = "prompt", required = false) String pPrompt,
                                             @RequestParam(value = "display", required = false) String pDisplay,
                                             @RequestParam(value = "id_token_hint", required = false) String pIdTokenHint,
                                             Authentication pAuthentication) {
        
        try {
            
            // Create a UserAuth and redirect
            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();
            this.userAuthorizationService.createOneUserAuthorization(userDetails, pClientPublicId, pScope);
            
            return authorize(pResponseType, pClientPublicId, pScope, pRedirectionUrl, pState, null, pPrompt, pDisplay, pIdTokenHint, pAuthentication, RequestMethod.POST);

        } catch (Exception pException) {

            throw new OpenIdExceptionBuilder(pException)
                    .clientId(pClientPublicId)
                    .redirection(pRedirectionUrl)
                    .state(pState)
                    .from(FlowUtils.getAuthorizationType(pResponseType))
                    .build();
        }
    }
    
    private RedirectView authorizeCodeResponse(Authentication pAuthentication,
                                               AuthRequest pAuthRequest,
                                               String pClientId,
                                               String pScopes,
                                               String pRedirectionUrl,
                                               String pState) {


        // Create auth code
        AegaeonUserDetails user = (AegaeonUserDetails) pAuthentication.getPrincipal();
        tokenServicesFacade.validateClientFlow(pClientId, GrantType.AUTHORIZATION_CODE);
        AuthorizationCodeView code = this.authorizationCodeService
                .createCode(user.getId(), pClientId, pAuthRequest.getResponseTypeParam(), pScopes, pRedirectionUrl);

        // Returned values
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("state", pState);
        params.add("code", code.getCode());

        String url = UriBuilder.build(pRedirectionUrl, params, false);
        RedirectView view = new RedirectView(url, false);

        return view;

    }
    
    private RedirectView implicitResponse(Authentication pAuthentication,
                                          AuthRequest pAuthRequest,
                                          String pClientId,
                                          String pScopes,
                                          String pRedirectionUrl,
                                          String pState) {

        TokenResponse token = this.tokenServicesFacade.createTokenForImplicit(pAuthRequest, pClientId, pScopes, pRedirectionUrl, pAuthentication);

        RedirectView view = new RedirectView(
                UriBuilder.build(pRedirectionUrl,
                        token,
                        pState),
                false);

        return view;
    }
    
    /**
     * Create a page to ask user consent.
     * See authorize endpoint.
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
