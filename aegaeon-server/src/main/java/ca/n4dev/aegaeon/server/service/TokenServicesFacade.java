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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.server.controller.dto.TokenResponse;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * TokensService.java
 * 
 * TokensService acts as a facade for AccessTokenService and RefreshTokenService and 
 * provide an easier way to deal with token to controllers.
 *
 * @author by rguillemette
 * @since Jun 3, 2017
 */
@Service
public class TokenServicesFacade {

    private IdTokenService idTokenService;
    private AccessTokenService accessTokenService;
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    public TokenServicesFacade(IdTokenService pIdTokenService, 
                               AccessTokenService pAccessTokenService, 
                               RefreshTokenService pRefreshTokenService) {
        
        this.idTokenService = pIdTokenService;
        this.accessTokenService = pAccessTokenService;
        this.refreshTokenService = pRefreshTokenService;
    }
    
    @Transactional
    public TokenResponse createTokenResponse(Flow pFlow,
                                             String pClientPublicId,
                                             Long pUserId,
                                             List<Scope> pScopes,
                                             String pRedirectUrl) {
        
        try {
            
            TokenResponse t = new TokenResponse();
            t.setScope(Utils.join(pScopes, s -> s.getName()));
            t.setTokenType(TokenResponse.BEARER);
            
            // Tokens
            IdToken idToken = this.idTokenService.createToken(pFlow, pUserId, pClientPublicId, pScopes);
            AccessToken accessToken = this.accessTokenService.createToken(pFlow, pUserId, pClientPublicId, pScopes);
            RefreshToken refreshToken = this.refreshTokenService.createToken(pFlow, pUserId, pClientPublicId, pScopes);
            
            t.setIdToken(idToken);
            t.setAccessToken(accessToken);
            t.setRefreshToken(refreshToken);
            
            // Time
            long expiresIn = ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.getValidUntil());
            t.setExpiresIn(String.valueOf(expiresIn));
            
            return t;
            
        } catch (ServerException se) {
            throw se;
        } catch (Exception e) {
            throw new OauthRestrictedException(getClass(),
                                               pFlow, 
                                               OAuthErrorType.server_error, 
                                               pClientPublicId, 
                                               pRedirectUrl, 
                                               e.getMessage());
        }
        
    }
}
