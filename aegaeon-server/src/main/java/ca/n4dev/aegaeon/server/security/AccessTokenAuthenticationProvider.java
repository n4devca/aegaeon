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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.nimbusds.jwt.SignedJWT;

import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.model.AccessToken;
import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.service.AccessTokenService;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;

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
    
    private AccessTokenService accessTokenService;
    private ClientService clientService;
    private TokenFactory tokenFactory;
    private ServerInfo serverInfo;
    
    public AccessTokenAuthenticationProvider(ClientService pClientService, 
                                             AccessTokenService pAccessTokenService,
                                             TokenFactory pTokenFactory,
                                             ServerInfo pServerInfo) {
        this.accessTokenService = pAccessTokenService;
        this.clientService = pClientService;
        this.tokenFactory = pTokenFactory;
        this.serverInfo = pServerInfo;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication pAuthentication) throws AuthenticationException {
                
        /*
         * JWT: 
         * t: not null
         * t: signed ok
         * t: date ok
         * t: issuer ok
         * db: still exists
         * 
         * Opaque:
         * db: still exists
         * db: date ok
         * 
         * */
        
        AccessTokenAuthentication accessTokenAuthentication = (AccessTokenAuthentication) pAuthentication;
        String token = accessTokenAuthentication.getAccessToken();
        SignedJWT signedJWT = null;

        try {
            signedJWT = SignedJWT.parse(token);
            
            // Try to get access token
            AccessToken accessToken = this.accessTokenService.findByTokenValue(token);
            
            // Exists ?
            if (accessToken == null) {
                throw new AuthenticationCredentialsNotFoundException(token + " is invalid or has been revoked.");
            }
            
            // Still Valid ?
            if (!Utils.isAfterNow(accessToken.getValidUntil())) {
                throw new AuthenticationCredentialsNotFoundException(token + " is expired.");
            }
            
            // Validate
            if (!this.tokenFactory.validate(accessToken.getClient(), token)) {
                throw new AccessTokenAuthenticationException("The JWT is not valid");
            }
            
            // OK, good
            User u = accessToken.getUser();
            List<String> roles = new ArrayList<>();
            
            if (u.getAuthorities() != null) {
                u.getAuthorities().forEach(a -> roles.add(a.getCode()));
            }

            Authentication auth = new AccessTokenAuthentication(u, 
                                                                token, 
                                                                accessToken.getScopeList(), 
                                                                roles);
            
            return auth;
            
        } catch (ParseException pe) {
            LOGGER.info("AccessTokenAuthenticationProvider#authenticate: unable to parse as JWT");
            throw new AccessTokenAuthenticationException("AccessTokenAuthenticationProvider#authenticate: unable to parse as JWT");
        } catch (Exception e) {
            LOGGER.info("AccessTokenAuthenticationProvider#authenticate: Error checking JWT token");
            throw new AccessTokenAuthenticationException("Error checking JWT token");
        }
    }
    

    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> pAuthentication) {
        return AccessTokenAuthentication.class.equals(pAuthentication);
    }

}
