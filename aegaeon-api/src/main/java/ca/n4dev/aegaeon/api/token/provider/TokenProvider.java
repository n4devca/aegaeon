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
package ca.n4dev.aegaeon.api.token.provider;

import java.time.temporal.TemporalUnit;
import java.util.Map;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;

/**
 * TokenProvider.java
 * 
 * A provider able to create a token from a oauth user and a oauth client.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
public interface TokenProvider {
    
    /**
     * @return The name of this provider. Must be unique.
     */
    String getAlgorithmName();
    
    /**
     * @return The type of token created by this provider.
     */
    TokenProviderType getType();
    
    /**
     * If this provider is enabled and has been correctly instanciate.
     * @return true or false.
     */
    boolean isEnabled();
    
    /**
     * Create a token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client used during authentication.
     * @return A token.
     */
    Token createToken(OAuthUser pOAuthUser, 
                      OAuthClient pOAuthClient, 
                      Long pTimeValue, 
                      TemporalUnit pTemporalUnit) throws Exception;
    
    /**
     * Create a token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client used during authentication.
     * @param pPayloads The payload to add to the token.
     * @return A token.
     */
    Token createToken(OAuthUser pOAuthUser, 
                      OAuthClient pOAuthClient, 
                      Long pTimeValue, 
                      TemporalUnit pTemporalUnit, 
                      Map<String, Object> pPayloads) throws Exception;
    
}
