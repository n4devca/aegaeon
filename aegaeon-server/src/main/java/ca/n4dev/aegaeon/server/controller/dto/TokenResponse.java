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
package ca.n4dev.aegaeon.server.controller.dto;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TokenResponse.java
 * 
 * A token response following a call to /token endpoint.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
public class TokenResponse {
    
   
    private String accessToken;
    
    private String tokenType;
    
    private String expiresIn;
        
    private List<String> scope;
    
    private String refreshToken;

    public TokenResponse() {}
    
    public TokenResponse(String pAccessToken, String pTokenType, String pExpiresIn, List<String> pScope, String pRefreshToken) {
        this.accessToken = pAccessToken;
        this.tokenType = pTokenType;
        this.expiresIn = pExpiresIn;
        this.scope = pScope;
        this.refreshToken = pRefreshToken;
    }
    
    /**
     * @return the accessToken
     */
    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param pAccessToken the accessToken to set
     */
    public void setAccessToken(String pAccessToken) {
        accessToken = pAccessToken;
    }

    /**
     * @return the tokenType
     */
    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @param pTokenType the tokenType to set
     */
    public void setTokenType(String pTokenType) {
        tokenType = pTokenType;
    }

    /**
     * @return the expiresIn
     */
    @JsonProperty("expires_in")
    public String getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param pExpiresIn the expiresIn to set
     */
    public void setExpiresIn(String pExpiresIn) {
        expiresIn = pExpiresIn;
    }

    /**
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param pRefreshToken the refreshToken to set
     */
    @JsonProperty("refresh_token")
    public void setRefreshToken(String pRefreshToken) {
        refreshToken = pRefreshToken;
    }

    /**
     * @param pScope the scope to set
     */
    public void setScope(List<String> pScope) {
        scope = pScope;
    }
    
    /**
     * @return the scope as string
     */
    @JsonProperty("scope")
    public String getScopeList() {
        if (this.scope != null) {
            return this.scope.stream().collect(Collectors.joining(" "));
        }
        return null;
    }
    
    public static TokenResponse bearer(String pAccessToken, String pExpiresIn, List<String> pScope) {
        return new TokenResponse(pAccessToken, "bearer", pExpiresIn, pScope, null);
    }
}
