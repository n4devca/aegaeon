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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * AccessTokenAuthFilter.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 16, 2017
 */
public class AccessTokenAuthenticationFilter extends GenericFilterBean {
    
    private static final String AUTH_HEADER_SCHEMA = "Bearer";
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_PARAM_NAME = "access_token";
    
    private AuthenticationEntryPoint authenticationEntryPoint;
    private AuthenticationManager authenticationManager;
    /**
     * 
     */
    @Autowired
    public AccessTokenAuthenticationFilter(AuthenticationManager pAuthenticationManager, 
                                 AuthenticationEntryPoint pAuthenticationEntryPoint) {
        
        this.authenticationEntryPoint = pAuthenticationEntryPoint;
        this.authenticationManager = pAuthenticationManager;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest pServletRequest, ServletResponse pServletResponse, FilterChain pFilterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) pServletRequest;
        HttpServletResponse response = (HttpServletResponse) pServletResponse;
        
        String header = (String) request.getHeader(AUTH_HEADER_NAME);
        String param  = request.getParameter(AUTH_PARAM_NAME);
        String accessToken = null;
        
        if (Utils.isNotEmpty(header)) {
            accessToken = extractAccessToken(header);
        } else if (Utils.isNotEmpty(param)) {
            accessToken = param;
        }
        
        if (Utils.isNotEmpty(accessToken)) {
            
            // Attempt authentication
            try {
                
                Authentication auth = attemptAuthentication(accessToken, pServletRequest, pServletResponse);
                SecurityContextHolder.getContext().setAuthentication(auth);  
                
            } catch (AuthenticationException ae) {
                this.authenticationEntryPoint.commence(request, response, ae);
            } catch (Exception  e) {
                this.authenticationEntryPoint.commence(request, response, new AccessTokenAuthenticationException(e));
            }
            
        }
        
        pFilterChain.doFilter(pServletRequest, pServletResponse);
    }

    private Authentication attemptAuthentication(String pAccessToken, ServletRequest pServletRequest, ServletResponse pServletResponse) throws AuthenticationException {
        
        Authentication auth = new AccessTokenAuthentication(pAccessToken);
        return this.authenticationManager.authenticate(auth);
    }
    
    private String extractAccessToken(String pAuthorizationHeader) {
        if (pAuthorizationHeader != null) {
            
            if (pAuthorizationHeader.indexOf(AUTH_HEADER_NAME) == -1) {
                throw new InsufficientAuthenticationException("Bearer not found");
            }
            return pAuthorizationHeader.substring(AUTH_HEADER_SCHEMA.length()).trim();
        }
        
        return null;
    }

}
