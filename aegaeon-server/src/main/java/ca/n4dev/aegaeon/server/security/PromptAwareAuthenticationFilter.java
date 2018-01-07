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
package ca.n4dev.aegaeon.server.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.protocol.Prompt;
import ca.n4dev.aegaeon.server.controller.AuthorizationController;
import ca.n4dev.aegaeon.server.controller.ControllerErrorInterceptor;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * PromptAwareAuthenticationFilter.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Aug 8, 2017
 */
public class PromptAwareAuthenticationFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptAwareAuthenticationFilter.class);
    
    private ClientService clientService;
    private ControllerErrorInterceptor controllerErrorInterceptor;
    
    public PromptAwareAuthenticationFilter(ClientService pClientService, ControllerErrorInterceptor pControllerErrorInterceptor) {
        this.clientService = pClientService;
        this.controllerErrorInterceptor = pControllerErrorInterceptor;
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest pServletRequest, ServletResponse pServletResponse, FilterChain pFilterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) pServletRequest;
        HttpServletResponse response = (HttpServletResponse) pServletResponse;
        
        
        
        // Only on /authorize
        String requestedPath = request.getServletPath();

        if (AuthorizationController.URL.startsWith(requestedPath)) {
            
            String ps = request.getParameter("prompt");
            String clientIdStr = request.getParameter("client_id");
            String redirectionUrl = request.getParameter("redirection_url");
            String responseType = request.getParameter("response_type");
            String state = request.getParameter("state");
            
            if (Utils.isNotEmpty(ps) && ps.equals(Prompt.none.toString()) && Utils.isNotEmpty(clientIdStr)) {
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                
                if (existingAuth == null) {
                    
                    Flow flow = FlowFactory.of(responseType);
                    Long clientId = null;
                    
                    try {
                        clientId = Long.parseLong(clientIdStr);                        
                    } catch (Exception e) {
                        LOGGER.warn("The client id cannot be converted: " + clientIdStr);
                    }
                    
                    if (clientId == null || !this.clientService.hasRedirectionUri(clientId, redirectionUrl)) {
                        
                        // Redirect properly
                        throw new OauthRestrictedException(getClass(),
                                flow, 
                                OAuthErrorType.invalid_request, 
                                clientIdStr, 
                                redirectionUrl,
                                "Invalid redirect_uri.");
                    }
                    
                    handleError(redirectionUrl, state, flow, request, response);
                    return;
                }            
            }
        }
        
        pFilterChain.doFilter(pServletRequest, pServletResponse);
    }
    
    private void handleError(String pRedirectionUrl,
                             String pState, 
                             Flow pFlow,
                             HttpServletRequest pHttpServletRequest, 
                             HttpServletResponse pHttpServletResponse) {
        
        OAuthPublicRedirectionException ex = new OAuthPublicRedirectionException(getClass(), pFlow, OAuthErrorType.login_required, pRedirectionUrl);
        
        RedirectView redirect = this.controllerErrorInterceptor.oauthPublicException(ex);
        
        try {
            pHttpServletResponse.sendRedirect(redirect.getUrl());            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
