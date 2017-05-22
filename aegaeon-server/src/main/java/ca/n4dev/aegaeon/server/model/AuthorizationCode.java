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

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * AuthorizationCode.java
 * 
 * An AutorizationCode is delivered after a successful authentication to
 * a client and allow the client to request an access token.
 *
 * @author by rguillemette
 * @since May 10, 2017
 */
@Entity
@Table(name = "authorization_code")
public class AuthorizationCode extends BaseEntity {

    private String code;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
    
    private LocalDateTime validUntil;
    
    /**
     * Default no-args constructor.
     */
    public AuthorizationCode() {}
    
    public AuthorizationCode(String pCode, User pUser, Client pClient) {
        this.code = pCode;
        this.user = pUser;
        this.client = pClient;
        this.validUntil = LocalDateTime.now().plusMinutes(3L); // default 3 minutes
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param pCode the code to set
     */
    public void setCode(String pCode) {
        code = pCode;
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
     * @return the validUntil
     */
    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    /**
     * @param pValidUntil the validUntil to set
     */
    public void setValidUntil(LocalDateTime pValidUntil) {
        validUntil = pValidUntil;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }
    
    
}
