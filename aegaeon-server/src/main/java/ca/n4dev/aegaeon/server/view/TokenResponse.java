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
package ca.n4dev.aegaeon.server.view;

import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.RefreshToken;
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

    public static final String BEARER = "Bearer";
    
    private String idToken;
    
    private String accessToken;
    
    private String tokenType;

    private Long expiresIn;
        
    private String scope;
    
    private String refreshToken;

    public TokenResponse() {}

    public TokenResponse(String pAccessToken, String pTokenType, Long pExpiresIn, String pScope, String pRefreshToken) {
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
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param pExpiresIn the expiresIn to set
     */
    public void setExpiresIn(Long pExpiresIn) {
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
    public void setScope(String pScope) {
        scope = pScope;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set the access token from en entity with null check.
     * @param pAccessToken The Access Token.
     */
    public void setAccessToken(AccessToken pAccessToken) {
        if (pAccessToken != null) {
            this.accessToken = pAccessToken.getToken();
        }
    }
    
    /**
     * Set the refresh token from en entity with null check.
     * @param pRefreshToken The Refresh Token.
     */
    public void setRefreshToken(RefreshToken pRefreshToken) {
        if (pRefreshToken != null) {
            this.refreshToken = pRefreshToken.getToken();
        }
    }

    /**
     * @return the idToken
     */
    @JsonProperty("id_token")
    public String getIdToken() {
        return this.idToken;
    }

    /**
     * @param pIdToken the idToken to set
     */
    public void setIdToken(IdToken pIdToken) {
        if (pIdToken != null) {
            this.idToken = pIdToken.getToken();            
        }
    }
    
    public String toString() {
        return new StringBuilder()
                        .append("TokenResponse")
                        .append(",")
                        .append(this.idToken != null ? "idt," : "")
                        .append(this.accessToken != null ? "act," : "")
                        .append(this.refreshToken != null ? "rft," : "")
                        .append(this.scope != null ? scope : "-")
                        .toString();
    }
}
