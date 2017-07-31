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

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.SignedJWT;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.api.token.verifier.TokenVerifier;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenFactory.class);
    
    private Map<TokenProviderType, TokenProvider> tokenProviderHolder;
    private Map<TokenProviderType, TokenVerifier> tokenVerifierHolder; 
    
    private KeysProvider keysProvider;
    
    /**
     * Default Constructor.
     * Take all TokenProvider and keep them in a map for easy access.
     * @param pTokenProviders The declared TokenProviders.
     */
    @Autowired
    public TokenFactory(KeysProvider pKeysProvider, List<TokenProvider> pTokenProviders, List<TokenVerifier> pTokenVerifiers) {
        this.tokenProviderHolder = new HashMap<>();
        this.tokenVerifierHolder = new HashMap<>();
        
        if (pTokenProviders != null) {
            for (TokenProvider t : pTokenProviders) {
                this.tokenProviderHolder.put(t.getType(), t);
            }
        }
        
        if (pTokenVerifiers != null) {
            for (TokenVerifier v : pTokenVerifiers) {
                this.tokenVerifierHolder.put(v.getType(), v);
            }
        }
        
        this.keysProvider = pKeysProvider;
    }
    
    /**
     * @return A uuid.
     */
    public String uniqueCode() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Validate and extract a token.
     * @param pToken The token.
     * @return A OAuthUser or null.
     */
    public boolean validate(OAuthClient pOAuthClient, String pTokenValue) {
        
        TokenProviderType type = TokenProviderType.from(pOAuthClient.getProviderName());
        TokenVerifier verifier = this.tokenVerifierHolder.get(type);
        
        if (verifier != null) {
            return verifier.validate(pTokenValue);            
        }
        
        return false;
    }
    
    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProvider A tokenProvider to create the token.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, TokenProvider pTokenProvider, 
                             Long pTimeValue, TemporalUnit pTemporalUnit, Map<String, String> pPayloads) throws Exception {
        
        
        return pTokenProvider.createToken(pOAuthUser, 
                                          pOAuthClient,
                                          pTimeValue,
                                          pTemporalUnit,
                                          pPayloads);
    }
    

    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, TokenProviderType pTokenProviderType, 
                             Long pTimeValue, TemporalUnit pTemporalUnit, Map<String, String> pPayloads) throws Exception {
        
        TokenProvider tp = this.tokenProviderHolder.get(pTokenProviderType);
        
        return tp.createToken(pOAuthUser, 
                              pOAuthClient,
                              pTimeValue,
                              pTemporalUnit,
                              pPayloads);
    }
    
    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, String pTokenProviderName, 
                             Long pTimeValue, TemporalUnit pTemporalUnit, Map<String, String> pPayloads) throws Exception {
        
        return createToken(pOAuthUser, 
                           pOAuthClient, 
                           TokenProviderType.from(pTokenProviderName), 
                           pTimeValue, 
                           pTemporalUnit, 
                           pPayloads);
    }
    
    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, 
                             Long pTimeValue, TemporalUnit pTemporalUnit, Map<String, String> pPayloads) throws Exception {
        
       
        return createToken(pOAuthUser, 
                pOAuthClient,
                TokenProviderType.from(pOAuthClient.getProviderName()),
                pTimeValue,
                pTemporalUnit,
                pPayloads);
    }
    
    public List<String> getSupportedAlgorithm() {
        List<String> algos = new ArrayList<>();
        
        this.tokenProviderHolder.entrySet().forEach(p -> algos.add(p.getValue().getAlgorithmName()));
        
        return algos;
    }
    
    public String publicJwks() throws Exception {
        return this.keysProvider.toPublicJson();
    }
    
    
    public OAuthUser extractAndValidate(OAuthClient pOAuthClient, String pTokenValue) throws Exception {
        
        return this.tokenVerifierHolder.get(pOAuthClient.getProviderName()).extractAndValidate(pTokenValue);
    }
}
