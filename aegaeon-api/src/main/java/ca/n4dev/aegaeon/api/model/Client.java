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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    
    @Column(name = "id_token_seconds")
    private Long idTokenSeconds;
    
    @Column(name = "access_token_seconds")
    private Long accessTokenSeconds;
    
    @Column(name = "refresh_token_seconds")
    private Long refreshTokenSeconds;
    
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<ClientRedirection> redirections;
    
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<ClientScope> scopes;
    
    @ManyToMany
    @JoinTable(name = "client_grant_type",
               joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "grant_type_id", referencedColumnName = "id"))
    private List<GrantType> grantTypes;
    
    @OneToMany(mappedBy = "client")
    private List<ClientContact> contacts;
    
    @OneToMany(mappedBy = "client")
    private List<ClientRequestedUri> requestedUris;
    
    
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
     * @return the redirections
     */
    public boolean hasRedirection(String pUrl) {
        
        if (this.redirections != null && pUrl != null && !pUrl.isEmpty()) {
            for (ClientRedirection cr : this.redirections) {
                if (pUrl.equals(cr.getUrl())) {
                    return true;
                }
            }
        }
        
        return false;
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
     * @return the scopes
     */
    public List<String> getScopesAsNameList() {
        if (scopes != null) {
            List<String> strs = new ArrayList<>();
            scopes.forEach(s -> strs.add(s.getScope().getName()));
            
            return strs;
        }
        
        return Collections.emptyList();
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

    /**
     * @return the refreshTokenSeconds
     */
    public Long getRefreshTokenSeconds() {
        return refreshTokenSeconds;
    }

    /**
     * @param pRefreshTokenSeconds the refreshTokenSeconds to set
     */
    public void setRefreshTokenSeconds(Long pRefreshTokenSeconds) {
        refreshTokenSeconds = pRefreshTokenSeconds;
    }

    /**
     * @return the grantTypes
     */
    public List<GrantType> getGrantTypes() {
        return grantTypes;
    }

    /**
     * @param pGrantTypes the grantTypes to set
     */
    public void setGrantTypes(List<GrantType> pGrantTypes) {
        grantTypes = pGrantTypes;
    }

    /**
     * @return the contacts
     */
    public List<ClientContact> getContacts() {
        return contacts;
    }

    /**
     * @param pContacts the contacts to set
     */
    public void setContacts(List<ClientContact> pContacts) {
        contacts = pContacts;
    }

    /**
     * @return the requestedUris
     */
    public List<ClientRequestedUri> getRequestedUris() {
        return requestedUris;
    }

    /**
     * @param pRequestedUris the requestedUris to set
     */
    public void setRequestedUris(List<ClientRequestedUri> pRequestedUris) {
        requestedUris = pRequestedUris;
    }

    /**
     * @return the idTokenSeconds
     */
    public Long getIdTokenSeconds() {
        return idTokenSeconds;
    }

    /**
     * @param pIdTokenSeconds the idTokenSeconds to set
     */
    public void setIdTokenSeconds(Long pIdTokenSeconds) {
        idTokenSeconds = pIdTokenSeconds;
    }
    
}
