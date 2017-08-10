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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * IntrospectResponse.java
 * 
 * An introspect response.
 *
 * @author by rguillemette
 * @since Aug 10, 2017
 */
public class IntrospectResponse {
    
    private boolean active;
    
    private String scope;
    
    private String username;
    
    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("exp")
    private Long expiration;
    
    private String sub;

    @JsonProperty("iat")
    private String issueAt;
    
    @JsonProperty("aud")
    private String audience;
    
    @JsonProperty("iss")
    private String issuer;
    
    /**
     * Basic constructor.
     * @param pActive If the token is actived.
     */
    public IntrospectResponse(boolean pActive) {
        this.active = pActive;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param pActive the active to set
     */
    public void setActive(boolean pActive) {
        active = pActive;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param pScope the scope to set
     */
    public void setScope(String pScope) {
        scope = pScope;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param pUsername the username to set
     */
    public void setUsername(String pUsername) {
        username = pUsername;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param pClientId the clientId to set
     */
    public void setClientId(String pClientId) {
        clientId = pClientId;
    }

    /**
     * @return the tokenType
     */
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
     * @return the expiration
     */
    public Long getExpiration() {
        return expiration;
    }

    /**
     * @param pExpiration the expiration to set
     */
    public void setExpiration(Long pExpiration) {
        expiration = pExpiration;
    }

    /**
     * @return the sub
     */
    public String getSub() {
        return sub;
    }

    /**
     * @param pSub the sub to set
     */
    public void setSub(String pSub) {
        sub = pSub;
    }

    /**
     * @return the issueAt
     */
    public String getIssueAt() {
        return issueAt;
    }

    /**
     * @param pIssueAt the issueAt to set
     */
    public void setIssueAt(String pIssueAt) {
        issueAt = pIssueAt;
    }

    /**
     * @return the audience
     */
    public String getAudience() {
        return audience;
    }

    /**
     * @param pAudience the audience to set
     */
    public void setAudience(String pAudience) {
        audience = pAudience;
    }

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @param pIssuer the issuer to set
     */
    public void setIssuer(String pIssuer) {
        issuer = pIssuer;
    }
    
}
