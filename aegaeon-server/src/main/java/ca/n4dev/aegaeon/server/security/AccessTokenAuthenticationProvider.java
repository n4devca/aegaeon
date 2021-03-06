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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.service.AuthenticationService;

/**
 * AccessTokenAuthenticationProvider.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 21, 2017
 */
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenAuthenticationProvider.class);
    
    private AuthenticationService authenticationService;
    
    public AccessTokenAuthenticationProvider(AuthenticationService pAuthenticationService,
                                             ServerInfo pServerInfo) {
        this.authenticationService = pAuthenticationService;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication pAuthentication) throws AuthenticationException {
       
        AccessTokenAuthentication accessTokenAuthentication = (AccessTokenAuthentication) pAuthentication;
        String token = accessTokenAuthentication.getAccessToken();

        return this.authenticationService.authenticate(token);
    }
    

    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> pAuthentication) {
        return AccessTokenAuthentication.class.equals(pAuthentication);
    }

}
