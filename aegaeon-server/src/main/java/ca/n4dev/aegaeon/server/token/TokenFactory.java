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
package ca.n4dev.aegaeon.server.token;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.api.token.provider.TokenProviderType;

/**
 * TokenFactory.java
 * 
 * A simple factory to create a Token from a user, a client and a tokenprovider.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
@Component
public class TokenFactory {

    private static final long DEFAULT_TIME_VALUE = 1L;
    private static final TemporalUnit DEFAULT_TIME_UNIT = ChronoUnit.HOURS;
    
    private Map<TokenProviderType, TokenProvider> tokenProviderHolder;
    
    /**
     * Default Constructor.
     * Take all TokenProvider and keep them in a map for easy access.
     * @param pTokenProviders The declared TokenProviders.
     */
    @Autowired
    public TokenFactory(List<TokenProvider> pTokenProviders) {
        this.tokenProviderHolder = new HashMap<>();
        
        if (pTokenProviders != null) {
            for (TokenProvider t : pTokenProviders) {
                this.tokenProviderHolder.put(t.getType(), t);
            }
        }
    }
    
    /**
     * @return A uuid.
     */
    public String uniqueCode() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProvider A tokenProvider to create the token.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, TokenProvider pTokenProvider, 
                             Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        return pTokenProvider.createToken(pOAuthUser, 
                                          pOAuthClient,
                                          pTimeValue,
                                          pTemporalUnit);
    }
    
    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, String pTokenProviderName, 
                             Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        
        TokenProvider tp = this.tokenProviderHolder.get(pTokenProviderName);
        
        return tp.createToken(pOAuthUser, 
                              pOAuthClient,
                              pTimeValue,
                              pTemporalUnit);
    }
    
    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, 
                             Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        
        TokenProvider tp = this.tokenProviderHolder.get(TokenProviderType.from(pOAuthClient.getProviderName()));
        
        return tp.createToken(pOAuthUser, 
                pOAuthClient,
                pTimeValue,
                pTemporalUnit);
    }
}
