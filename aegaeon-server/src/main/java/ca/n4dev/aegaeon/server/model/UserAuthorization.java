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
package ca.n4dev.aegaeon.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * UserAuthorization.java
 * 
 * Entity representing a user authorizing a client.
 *
 * @author by rguillemette
 * @since May 13, 2017
 */
@Entity
@Table(name = "users_authorization")
public class UserAuthorization extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
    
    @Column(name = "client_id", insertable = false, updatable = false)
    private Long clientId;
    
    private String scopes;
    
    /**
     * Default Constructor.
     */
    public UserAuthorization() {}
    
    /**
     * Build a user authorization.
     * @param pUser The user
     * @param pClient The client being authorized.
     * @param pScopes Scope list as string split by space.
     */
    public UserAuthorization(User pUser, Client pClient, String pScopes) {
        this.user = pUser;
        this.client = pClient;
        this.scopes = pScopes;
    }
    
    /**
     * Build a user authorization.
     * @param pUserId The user's id
     * @param pClient The client's id being authorized.
     * @param pScopes Scope list as string split by space.
     */
    public UserAuthorization(Long pUserId, Long pClientId, String pScopes) {
        this(new User(pUserId), new Client(pClientId), pScopes);
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
     * Simple lookup to check if a scope is authorized.
     * @param pScopeStr The scope name as string.
     * @return true or false.
     */
    public boolean hasScope(String pScopeStr) {
        if (this.scopes != null && this.scopes.contains(pScopeStr)) {
            return true;
        }
        
        return false;
    }

    /**
     * @return as String
     */
    public String toString() {
        
        return new StringBuilder()
                    .append("UserAuthorization")
                    .append(",")
                    .append(getUserId())
                    .append(",")
                    .append(getClientId())
                    .append(",")
                    .append(this.scopes != null ? this.scopes : "-")
                    .toString();
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return the clientId
     */
    public Long getClientId() {
        return clientId;
    }
}
