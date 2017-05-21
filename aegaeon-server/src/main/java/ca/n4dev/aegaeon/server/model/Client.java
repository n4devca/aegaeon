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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ca.n4dev.aegaeon.api.token.OAuthClient;

/**
 * Client.java
 * 
 * Represent a client allowed to authorize a user.
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Entity
@Table(name = "client")
public class Client extends BaseEntity implements OAuthClient {

    @Column(name = "public_id")
    private String publicId;
    
    private String secret;
    
    private String name;
    
    private String description;
    
    private String logoUrl;
    
    @Column(name = "provider_name")
    private String providerName;
    
    @Column(name = "access_token_seconds")
    private Long accessTokenSeconds;
    
    @ManyToOne
    @JoinColumn(name = "client_type_id")
    private ClientType clientType;
    
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<ClientRedirection> redirections;
    
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<ClientScope> scopes;
    
    public Client() {}
    
    public Client(Long pId) {
        this.setId(pId);
    }
    

    /**
     * @return the publicId
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * @param pPublicId the publicId to set
     */
    public void setPublicId(String pPublicId) {
        publicId = pPublicId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param pName the name to set
     */
    public void setName(String pName) {
        name = pName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param pDescription the description to set
     */
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    /**
     * @return the logoUrl
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param pLogoUrl the logoUrl to set
     */
    public void setLogoUrl(String pLogoUrl) {
        logoUrl = pLogoUrl;
    }

    /**
     * @return the clientType
     */
    public ClientType getClientType() {
        return clientType;
    }

    /**
     * @param pClientType the clientType to set
     */
    public void setClientType(ClientType pClientType) {
        clientType = pClientType;
    }

    /**
     * @return the redirections
     */
    public List<ClientRedirection> getRedirections() {
        return redirections;
    }

    /**
     * @param pRedirections the redirections to set
     */
    public void setRedirections(List<ClientRedirection> pRedirections) {
        redirections = pRedirections;
    }

    /**
     * @return the scopes
     */
    public List<ClientScope> getScopes() {
        return scopes;
    }

    /**
     * @param pScopes the scopes to set
     */
    public void setScopes(List<ClientScope> pScopes) {
        scopes = pScopes;
    }

    /**
     * @return the providerName
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * @param pProviderName the providerName to set
     */
    public void setProviderName(String pProviderName) {
        providerName = pProviderName;
    }

    /**
     * @return the secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * @param pSecret the secret to set
     */
    public void setSecret(String pSecret) {
        secret = pSecret;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.OAuthClient#getClientId()
     */
    @Override
    public String getClientId() {
        return this.publicId;
    }

    /**
     * @return the accessTokenSeconds
     */
    public Long getAccessTokenSeconds() {
        return accessTokenSeconds;
    }

    /**
     * @param pAccessTokenSeconds the accessTokenSeconds to set
     */
    public void setAccessTokenSeconds(Long pAccessTokenSeconds) {
        accessTokenSeconds = pAccessTokenSeconds;
    }
    
}
