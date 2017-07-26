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
package ca.n4dev.aegaeon.api.token.verifier;

import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.TokenProviderType;

/**
 * TokenVerifier.java
 * 
 * A verifier able to validate a token and extract information.
 *
 * @author by rguillemette
 * @since Jul 18, 2017
 */
public interface TokenVerifier {

    /**
     * @return The name of this verifier. Must be unique.
     */
    String getVerifierName();
    
    /**
     * @return The type of token managed by this verifier.
     */
    TokenProviderType getType();
    
    /**
     * @return if this verifier is enable and correctly initialize.
     */
    boolean isEnable();
    
    /**
     * Validate a token. This function should return false if the token is not 
     * a JWT token. If the value is a jwt token, the claims must be extract and 
     * date must be check.
     * 
     * @param pToken The token to validate.
     * @return true or false.
     */
    boolean validate(String pToken);
    
    /**
     * Extract a jwt token.
     * @param pToken The token to extract.
     * @return An OAuthUser created from the jwt token or null.
     */
    OAuthUser extract(String pToken);
    
    /**
     * Extract a jwt token, then validate it.
     * @param pToken The token to extract.
     * @return An OAuthUser created from the jwt token or null.
     */
    OAuthUser extractAndValidate(String pToken);
}
