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
package ca.n4dev.aegaeon.server.logging;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import ca.n4dev.aegaeon.api.logging.AuthenticationLogger;
import ca.n4dev.aegaeon.api.logging.AuthorizationLogger;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.logging.ProtocolErrorLogger;
import ca.n4dev.aegaeon.api.logging.RequestEventLogger;
import ca.n4dev.aegaeon.api.logging.TokenGrantingLogger;

/**
 * SimpleOpenIdLogger.java
 * 
 * Implement OpenIdEventLogger and log using slf4j.
 *
 * @author by rguillemette
 * @since Jun 16, 2017
 */
@Component
public class SimpleOpenIdLogger implements OpenIdEventLogger {
    
    private static final String DASH = "-";

    private static final Map<OpenIdEvent, Logger> LOGGERS;
    
    // Keep ref to prevent map lookup
    private static final Logger AUTHENTICATION_LOGGER = LoggerFactory.getLogger(AuthenticationLogger.class);
    
    static {
        
        Logger errLogger = LoggerFactory.getLogger(ProtocolErrorLogger.class);
        Logger generalLogger = LoggerFactory.getLogger(SimpleOpenIdLogger.class);
        
        LOGGERS = new HashMap<>();
        LOGGERS.put(OpenIdEvent.AUTHENTICATION, AUTHENTICATION_LOGGER);
        LOGGERS.put(OpenIdEvent.AUTHORIZATION, LoggerFactory.getLogger(AuthorizationLogger.class));
        LOGGERS.put(OpenIdEvent.TOKEN_GRANTING, LoggerFactory.getLogger(TokenGrantingLogger.class));
        LOGGERS.put(OpenIdEvent.TOKEN_DENYING, LoggerFactory.getLogger(TokenGrantingLogger.class));
        LOGGERS.put(OpenIdEvent.REQUEST_INFO, LoggerFactory.getLogger(RequestEventLogger.class));
        LOGGERS.put(OpenIdEvent.RESTRICTED_ERROR, errLogger);
        LOGGERS.put(OpenIdEvent.PUBLIC_ERROR, errLogger);
        LOGGERS.put(OpenIdEvent.OTHERS, generalLogger);
    }
    
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.logging.OpenIdEventLogger#log(ca.n4dev.aegaeon.api.logging.OpenIdEvent, java.lang.Class, org.springframework.security.core.userdetails.UserDetails, java.lang.Object)
     */
    @Override
    public void log(OpenIdEvent pOpenIdEvent, Class<?> pSource, String pUser, Object pDetails) {
                
        LOGGERS.get(pOpenIdEvent).info(
                new StringBuilder()
                        .append(pOpenIdEvent.toString())
                        .append(SEPARATOR)
                        .append(pSource != null ? pSource.getSimpleName() : DASH)
                        .append(SEPARATOR)
                        .append(pUser != null ? pUser : DASH)
                        .append(SEPARATOR)
                        .append(pDetails != null ? pDetails.toString() : DASH)
                        .toString());
    }

    @EventListener
    public void handleAuthenticationEvent(AuthenticationSuccessEvent pAuthenticationSuccessEvent) {
        AUTHENTICATION_LOGGER.info(pAuthenticationSuccessEvent.getAuthentication().getPrincipal().toString());
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.logging.OpenIdEventLogger#log(ca.n4dev.aegaeon.api.logging.OpenIdEvent, java.lang.Class)
     */
    @Override
    public void log(OpenIdEvent pOpenIdEvent, Class<?> pSource) {
        this.log(pOpenIdEvent, pSource, DASH, DASH);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.logging.OpenIdEventLogger#log(ca.n4dev.aegaeon.api.logging.OpenIdEvent, java.lang.Class, java.lang.Object)
     */
    @Override
    public void log(OpenIdEvent pOpenIdEvent, Class<?> pSource, Object pDetails) {
        this.log(pOpenIdEvent, pSource, DASH, pDetails);
    }
    
}
