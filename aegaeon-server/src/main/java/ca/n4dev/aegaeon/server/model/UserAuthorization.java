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
    
    /**
     * Default Constructor.
     */
    public UserAuthorization() {}
    
    /**
     * Build a user authorization.
     * @param pUser
     * @param pClient
     */
    public UserAuthorization(User pUser, Client pClient) {
        this.user = pUser;
        this.client = pClient;
    }
    
    /**
     * Build a user authorization.
     * @param pUser
     * @param pClient
     */
    public UserAuthorization(Long pUserId, Long pClientId) {
        this.user = new User(pUserId);
        this.client = new Client(pClientId);
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
    
    
}
