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
package ca.n4dev.aegaeon.server.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthentication;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthenticationException;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.UserView;
import ca.n4dev.aegaeon.server.view.mapper.UserMapper;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthenticationService.java
 * 
 * A service to check and create an authentication from an access token.
 *
 * @author by rguillemette
 * @since Dec 14, 2017
 */
@Service
public class AuthenticationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private AccessTokenService accessTokenService;
    private ScopeService scopeService;
    private TokenFactory tokenFactory;
    private UserMapper userMapper;
    
    public AuthenticationService(AccessTokenService pAccessTokenService, 
                                 ScopeService pScopeService, 
                                 TokenFactory pTokenFactory, 
                                 UserMapper pUserMapper) {
        
        this.accessTokenService = pAccessTokenService;
        this.scopeService = pScopeService;
        this.tokenFactory = pTokenFactory;
        this.userMapper = pUserMapper;
    }
    
    @Transactional(readOnly = true)
    public Authentication authenticate(String pAccessToken) {
        
        AccessToken accessToken = null;
        String tokenStr = pAccessToken == null ? "-" : pAccessToken;
        
        try {
            if (Utils.isNotEmpty(pAccessToken)) {
                
                // Should be parseable
                SignedJWT.parse(pAccessToken);
                
                // Now Get it
                accessToken = this.accessTokenService.findByToken(pAccessToken);
            }
            
            // Exists ?
            if (accessToken == null) {
                throw new AuthenticationCredentialsNotFoundException(tokenStr + " is invalid or has been revoked.");
            }
            
            // Still Valid ?
            if (!Utils.isAfterNow(accessToken.getValidUntil())) {
                throw new AuthenticationCredentialsNotFoundException(tokenStr + " is expired.");
            }
            
            // Validate
            if (!this.tokenFactory.validate(accessToken.getClient(), pAccessToken)) {
                throw new AccessTokenAuthenticationException("The JWT is not valid");
            }
            
            User u = accessToken.getUser();
            List<String> roles = new ArrayList<>();
            
            if (u.getAuthorities() != null) {
                u.getAuthorities().forEach(a -> roles.add(a.getCode()));
            }
            UserView uv = this.userMapper.toView(u, null);
            
            Authentication auth = 
                    new AccessTokenAuthentication(
                            uv,
                            pAccessToken,
                            this.scopeService.getValidScopes(accessToken.getScopes()),
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
}
