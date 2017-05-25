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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.api.protocol.AuthorizationGrant;
import ca.n4dev.aegaeon.server.exception.OAuthPublicException;
import ca.n4dev.aegaeon.server.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.server.utils.UriBuilder;

/**
 * ControllerErrorInterceptor.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 12, 2017
 */
@ControllerAdvice
public class ControllerErrorInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerErrorInterceptor.class);

    private static final String HASHTAG = "#";
    private static final String QUESTIONMARK = "?";
    
    /**
     * 
     * @param pOAuthPublicException
     * @return
     */
    @ExceptionHandler(OAuthPublicException.class)
    public RedirectView oauthPublicException(final OAuthPublicException pOAuthPublicException) {
        
        String url = UriBuilder.build(pOAuthPublicException.getRedirectUrl(), pOAuthPublicException);
        
        if (pOAuthPublicException.getGrantType() == AuthorizationGrant.AUTHORIZATIONCODE) {
            return new RedirectView(url, false);            
        } else if (pOAuthPublicException.getGrantType() == AuthorizationGrant.IMPLICIT) {
            // TODO(RG): this smell bad
            url = url.replace(QUESTIONMARK, HASHTAG);
            return new RedirectView(url, false);
        } else {
            // TODO(RG) Client Cred: check what we should do in this case
            return new RedirectView(url, false);
        }
    }
    
    /**
     * 
     * @param pOAuthPublicException
     * @return
     */
    @ExceptionHandler(OauthRestrictedException.class)
    public ModelAndView oauthRestrictedException(final OauthRestrictedException pOauthRestrictedException) {
        ModelAndView mv = new ModelAndView("error");
        
        mv.addObject("type", "OauthRestrictedException");
        mv.addObject("error", pOauthRestrictedException);
        
        return mv;
    }
    
    @ExceptionHandler(Throwable.class)
    public ModelAndView exception(final Throwable pThrowable) {
        LOGGER.error("Generic Exception", pThrowable);
        
        ModelAndView mv = new ModelAndView("error");
        String errorMessage = (pThrowable != null ? pThrowable.getMessage() : "Unknown error");
        
        mv.addObject("type", "Throwable");
        mv.addObject("error", pThrowable);
        mv.addObject("errorMessage", errorMessage);
        
        return mv;
    }
}
