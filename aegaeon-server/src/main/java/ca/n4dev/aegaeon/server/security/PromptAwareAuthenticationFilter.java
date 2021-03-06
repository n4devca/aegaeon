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
package ca.n4dev.aegaeon.server.security;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ca.n4dev.aegaeon.api.exception.OpenIdExceptionBuilder;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.server.controller.AuthorizationController;
import ca.n4dev.aegaeon.server.controller.ControllerErrorInterceptor;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * PromptAwareAuthenticationFilter.java
 * <p>
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Aug 8, 2017
 */
public class PromptAwareAuthenticationFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptAwareAuthenticationFilter.class);

    private UserAuthorizationService userAuthorizationService;
    private ControllerErrorInterceptor controllerErrorInterceptor;

    /**
     * Constructor.
     *
     * @param pUserAuthorizationService       The authorization service.
     * @param pControllerErrorInterceptor The ControllerErrorInterceptor to handle error.
     */
    @Autowired
    public PromptAwareAuthenticationFilter(UserAuthorizationService pUserAuthorizationService,
                                           ControllerErrorInterceptor pControllerErrorInterceptor) {
        userAuthorizationService = pUserAuthorizationService;
        controllerErrorInterceptor = pControllerErrorInterceptor;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest pServletRequest, ServletResponse pServletResponse, FilterChain pFilterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) pServletRequest;
        HttpServletResponse response = (HttpServletResponse) pServletResponse;

        // Only on /authorize
        String requestedPath = request.getRequestURI();

        if (AuthorizationController.URL.startsWith(requestedPath)) {

            String prompt = request.getParameter(UriBuilder.PARAM_PROMPT);
            String clientIdStr = request.getParameter(UriBuilder.PARAM_CLIENT_ID);
            String redirectionUrl = request.getParameter(UriBuilder.PARAM_REDIRECTION_URL);
            String responseType = request.getParameter(UriBuilder.PARAM_RESPONSE_TYPE);
            String state = request.getParameter(UriBuilder.PARAM_STATE);
            String nonce = request.getParameter(UriBuilder.PARAM_NONCE);
            String scope = request.getParameter(UriBuilder.PARAM_SCOPE);


            AuthRequest authRequest = new AuthRequest(responseType, scope,
                                                      clientIdStr, redirectionUrl, state, nonce, null,
                                                      prompt, null, null);

            // none
            // => check session, client and redirection
            // login
            // => force sign-in

            // Client id and url need to be valid, otherwise, we don't redirect or response
            if (isValidRequest(authRequest)) {

                if (Utils.equals(prompt, Prompt.none.toString())) {

                    if (!isAuthorizedAlready(clientIdStr, redirectionUrl, scope)) {
                        handleError(authRequest, request, response);
                        return;
                    }

                } else if (Utils.equals(prompt, Prompt.login.toString())) {
                    clearUserContext(request);
                    // Nothing else to do, next filter should ask user to login
                }
            }
        }

        pFilterChain.doFilter(pServletRequest, pServletResponse);
    }

    private void handleError(AuthRequest pAuthRequest,
                             HttpServletRequest pHttpServletRequest,
                             HttpServletResponse pHttpServletResponse) {

        try {

            GrantType grantType = FlowUtils.getAuthorizationType(pAuthRequest);

            Object response =
                    this.controllerErrorInterceptor
                            .openIdException(new OpenIdExceptionBuilder()
                                                     .code(ServerExceptionCode.USER_UNAUTHENTICATED)
                                                     .redirection(pAuthRequest.getRedirectUri())
                                                     .from(grantType)
                                                     .state(pAuthRequest.getState())
                                                     .build(),
                                             Locale.ENGLISH,
                                             pHttpServletRequest,
                                             pHttpServletResponse);

            // TODO(RG) : other response ?
            if (response instanceof RedirectView) {
                pHttpServletResponse.sendRedirect(((RedirectView) response).getUrl());
            } else if (response instanceof ModelAndView) {
                // Redirect to error
                ModelAndView mv = (ModelAndView) response;
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                mv.getModel().forEach((pK, pV) -> {

                    // Convert String params
                    if (pV instanceof String) {
                        params.add(pK.toLowerCase(), (String) pV);
                    }
                });

                String url = UriBuilder.build("/" + mv.getViewName(), params, false);
                pHttpServletResponse.sendRedirect(url);
            }

        } catch (Exception pException) {
            LOGGER.error("PromptAwareAuthenticationFilter#handleError has failed", pException);
        }

    }


    private boolean isAuthorizedAlready(String pClientId, String pRedirectionUrl, String pScopeParam) {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if (existingAuth == null) {
            return userAuthorizationService.isAuthorized(existingAuth, pClientId, pRedirectionUrl, pScopeParam);
        }

        return false;
    }

    private boolean isValidRequest(AuthRequest pAuthRequest) {


        boolean hasProperParams = !Utils.areOneEmpty(pAuthRequest.getPrompt(),
                                                     pAuthRequest.getClientId(),
                                                     pAuthRequest.getRedirectUri(),
                                                     pAuthRequest.getResponseType());

        if (hasProperParams) {
            // OK, then, check the client
            return userAuthorizationService.isClientInfoValid(pAuthRequest.getClientId(), pAuthRequest.getRedirectUri());
        }

        return false;
    }

    private void clearUserContext(HttpServletRequest pRequest) {
        HttpSession session = pRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
