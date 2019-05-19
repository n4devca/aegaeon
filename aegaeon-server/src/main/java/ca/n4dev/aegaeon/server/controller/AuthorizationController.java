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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.n4dev.aegaeon.api.exception.UserUnauthorizedException;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.api.exception.InternalAuthorizationException;
import ca.n4dev.aegaeon.api.exception.InvalidClientIdException;
import ca.n4dev.aegaeon.api.exception.InvalidClientRedirectionException;
import ca.n4dev.aegaeon.api.exception.InvalidFlowException;
import ca.n4dev.aegaeon.api.exception.InvalidRequestMethodException;
import ca.n4dev.aegaeon.api.exception.InvalidScopeException;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import ca.n4dev.aegaeon.server.service.AuthorizationCodeService;
import ca.n4dev.aegaeon.server.service.BaseTokenService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.service.TokenServicesFacade;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.AuthorizationCodeView;
import ca.n4dev.aegaeon.server.view.TokenResponse;
import ca.n4dev.aegaeon.server.view.UserView;
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
 * AuthorizationController.java
 * <p>
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

    private static final String CONSENT_VIEW = "consent";

    private UserAuthorizationService userAuthorizationService;
    private AuthorizationCodeService authorizationCodeService;
    private TokenServicesFacade tokenServicesFacade;
    private ScopeService scopeService;
    private UserService userService;

    @Autowired
    public AuthorizationController(UserAuthorizationService pUserAuthorizationService,
                                   AuthorizationCodeService pAuthorizationCodeService,
                                   ScopeService pScopeService,
                                   TokenServicesFacade pTokenServicesFacade,
                                   UserService pUserService) {

        userAuthorizationService = pUserAuthorizationService;
        authorizationCodeService = pAuthorizationCodeService;
        scopeService = pScopeService;
        tokenServicesFacade = pTokenServicesFacade;
        userService = pUserService;
    }

    /*
     *
     * */
    @RequestMapping(value = "")
    public ModelAndView authorize(@RequestParam(value = "response_type", required = false) String pResponseType,
                                  @RequestParam(value = "scope", required = false) String pScope,
                                  @RequestParam(value = "client_id", required = false) String pClientPublicId,
                                  @RequestParam(value = "redirect_uri", required = false) String pRedirectUri,
                                  @RequestParam(value = "state", required = false) String pState,
                                  @RequestParam(value = "nonce", required = false) String pNonce,
                                  @RequestParam(value = "display", required = false) String pDisplay,
                                  @RequestParam(value = "prompt", required = false) String pPrompt,
                                  @RequestParam(value = "id_token_hint", required = false) String pIdTokenHint,
                                  Authentication pAuthentication,
                                  RequestMethod pRequestMethod) {


        RedirectView redirect = null;
        AuthRequest authRequest = new AuthRequest(pResponseType, pScope, pClientPublicId, pRedirectUri,
                                                  pState, pNonce, pDisplay, pPrompt, pIdTokenHint, pRequestMethod);


        // TODO(RG) : a bit of clean-up here would be cool
        // Validate basic info from request
        Assert.notEmpty(pClientPublicId, () -> new InvalidClientIdException(authRequest));
        Assert.notEmpty(pRedirectUri, () -> new InvalidClientRedirectionException(authRequest));

        // Make sure the client and redirection is valid
        if (!userAuthorizationService.isClientInfoValid(pClientPublicId, pRedirectUri)) {
            throw new InvalidClientRedirectionException(authRequest);
        }

        Assert.notEmpty(pScope, () -> new InvalidScopeException(pScope, authRequest));
        Assert.notEmpty(pResponseType, () -> new InvalidFlowException(authRequest));
        Assert.isTrue(pRequestMethod == RequestMethod.GET || pRequestMethod == RequestMethod.POST,
                      () -> new InvalidRequestMethodException(authRequest, pRequestMethod));

        // Parse and validate early
        GrantType grantType = FlowUtils.getAuthorizationType(authRequest);

        Set<String> exclusion = GrantType.IMPLICIT == grantType ? Utils.asSet(BaseTokenService.OFFLINE_SCOPE) : null;
        final ScopeService.ScopeSet scopeSet = scopeService.validate(pScope, exclusion);
        Assert.isTrue(scopeSet.getInvalidScopes().isEmpty(), () -> {
            final String invalidScope = Utils.join(scopeSet.getInvalidScopes(), pScopeView -> pScopeView.getName());
            return new InvalidScopeException(invalidScope, authRequest);
        });

        Assert.notNull(grantType, () -> new InvalidFlowException(authRequest));

        boolean isAlreadyAuthorized = this.userAuthorizationService.isAuthorized(pAuthentication,
                                                                                 pClientPublicId,
                                                                                 pRedirectUri,
                                                                                 pScope);

        try {
            if (authRequest.getPromptType() != null) {

                Prompt prompt = authRequest.getPromptType();

                if (prompt == Prompt.none && !isAlreadyAuthorized) {
                    throw new UserUnauthorizedException(authRequest);
                } else if (!isAlreadyAuthorized || prompt == Prompt.login || prompt == Prompt.consent) {
                    return consentPage(pAuthentication, authRequest);
                }  // else OK

            } else if (!isAlreadyAuthorized) {
                return consentPage(pAuthentication, authRequest);
            }

            // TODO(RG): Hybrid Flow


            switch (grantType) {

                case AUTHORIZATION_CODE:
                    redirect = authorizeCodeResponse(authRequest, pAuthentication);
                    break;

                case IMPLICIT:
                    redirect = implicitResponse(authRequest, pAuthentication);
                    break;

                default:
                    throw new InvalidFlowException(authRequest);
            }

            return new ModelAndView(redirect);

            // TODO(RG): this is wrong, we catch all exception and disrupt ControllerErrorInterceptor job
        } catch (Exception pException) {
            throw new InternalAuthorizationException(authRequest, pException);
        }

    }


    @RequestMapping(value = "/consent")
    public ModelAndView addUserAuthorization(@RequestParam("response_type") String pResponseType,
                                             @RequestParam("client_id") String pClientPublicId,
                                             @RequestParam(value = "scope", required = false) String pScopes,
                                             @RequestParam(value = "redirect_uri", required = false) String pRedirectionUrl,
                                             @RequestParam(value = "state", required = false) String pState,
                                             @RequestParam(value = "nonce", required = false) String pNonce,
                                             @RequestParam(value = "prompt", required = false) String pPrompt,
                                             @RequestParam(value = "display", required = false) String pDisplay,
                                             @RequestParam(value = "id_token_hint", required = false) String pIdTokenHint,
                                             @RequestParam(value = "accept_scopes", required = false) String[] pAcceptScopes,
                                             Authentication pAuthentication) {

        try {

            // Create a UserAuth and redirect
            final List<String> acceptedScopes = new ArrayList<>(Arrays.asList(pAcceptScopes));
            if (Utils.isNotEmpty(pScopes) && pScopes.contains("openid")) {
                acceptedScopes.add(0, "openid");
            }
            final String acceptedScopeParam = Utils.join(acceptedScopes, pScope -> pScope);
            AegaeonUserDetails userDetails = (AegaeonUserDetails) pAuthentication.getPrincipal();
            this.userAuthorizationService.createOneUserAuthorization(userDetails, pClientPublicId, acceptedScopeParam);

            return authorize(pResponseType, acceptedScopeParam, pClientPublicId, pRedirectionUrl, pState, pNonce, pDisplay, pPrompt, pIdTokenHint,
                             pAuthentication, RequestMethod.POST);

        } catch (InternalAuthorizationException pInternalAuthorizationException) {
            throw pInternalAuthorizationException;
        } catch (Exception pException) {
            throw new InternalAuthorizationException(null, pException);
        }
    }

    private RedirectView authorizeCodeResponse(AuthRequest pAuthRequest,
                                               Authentication pAuthentication) {


        // Create auth code
        AegaeonUserDetails user = (AegaeonUserDetails) pAuthentication.getPrincipal();

        tokenServicesFacade.validateClientFlow(pAuthRequest, pAuthRequest.getClientId(), Flow.authorization_code);
        AuthorizationCodeView code = this.authorizationCodeService.createCode(user.getId(),
                                                                              pAuthRequest);

        // Returned values
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(UriBuilder.PARAM_STATE, pAuthRequest.getState());
        params.add(UriBuilder.PARAM_CODE, code.getCode());

        String url = UriBuilder.build(pAuthRequest.getRedirectUri(), params, false);
        RedirectView view = new RedirectView(url, false);

        return view;

    }

    private RedirectView implicitResponse(AuthRequest pAuthRequest,
                                          Authentication pAuthentication) {

        TokenResponse token = this.tokenServicesFacade.createTokenForImplicit(pAuthRequest, pAuthentication);

        RedirectView view = new RedirectView(
                UriBuilder.build(pAuthRequest.getRedirectUri(),
                                 token,
                                 pAuthRequest.getState(),
                                 true),
                false);

        return view;
    }

    /**
     * Create a page to ask user consent.
     * See authorize endpoint.
     *
     * @param pAuthentication   The authenticated user.
     * @param pAuthRequest The authorization request.
     * @return A model and view.
     */
    private ModelAndView consentPage(Authentication pAuthentication,
                                     AuthRequest pAuthRequest) {


        ModelAndView authPage = new ModelAndView(CONSENT_VIEW);

        authPage.addObject("client_id", pAuthRequest.getClientId());
        authPage.addObject("redirect_uri", pAuthRequest.getRedirectUri());
        authPage.addObject("scope", pAuthRequest.getScope());
        authPage.addObject("state", pAuthRequest.getState());
        authPage.addObject("nonce", pAuthRequest.getNonce());
        authPage.addObject("response_type", pAuthRequest.getResponseType());
        authPage.addObject("display", pAuthRequest.getDisplay());
        authPage.addObject("prompt", pAuthRequest.getPrompt());


        final List<String> explode = Utils.explode(pAuthRequest.getScope(), pS -> pS);
        final UserView user = userService.findOne(((AegaeonUserDetails) pAuthentication.getPrincipal()).getId());
        final Map<String, Map<String, Object>> payload = userService.createPayload(user, new HashSet<>(explode));

        authPage.addObject("claims", payload);

        return authPage;
    }

}
