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
package ca.n4dev.aegaeon.api.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import ca.n4dev.aegaeon.api.token.TokenType;

/**
 * BaseToken.java
 * 
 * A basic class with all attributes common to tokens.
 *
 * @author by rguillemette
 * @since Dec 12, 2017
 */
@MappedSuperclass
public abstract class BaseTokenEntity extends BaseEntity {

    protected String token;
    
    protected ZonedDateTime validUntil;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    protected Client client;
    
    protected String scopes;

    /**
     * @return The implementation token type.
     */
    public abstract TokenType getTokenType();

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param pToken the token to set
     */
    public void setToken(String pToken) {
        token = pToken;
    }

    /**
     * @return the validUntil
     */
    public ZonedDateTime getValidUntil() {
        return validUntil;
    }

    /**
     * @param pValidUntil the validUntil to set
     */
    public void setValidUntil(ZonedDateTime pValidUntil) {
        validUntil = pValidUntil;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param pUser the user to set
     */
    public void setUser(User pUser) {
        user = pUser;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param pClient the client to set
     */
    public void setClient(Client pClient) {
        client = pClient;
    }

    /**
     * @return the scopes
     */
    public String getScopes() {
        return scopes;
    }

    /**
     * @param pScopes the scopes to set
     */
    public void setScopes(String pScopes) {
        scopes = pScopes;
    }
    
    /**
     * @return the scopes
     */
    public List<String> getScopeList() {
        if (this.scopes == null || this.scopes.isEmpty()) {
            return Collections.emptyList();
        }
        
        return Arrays.asList(scopes.split(" "));
    }
}
