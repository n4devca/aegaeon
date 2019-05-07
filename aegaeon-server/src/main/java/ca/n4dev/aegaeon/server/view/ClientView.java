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

import java.util.List;

import ca.n4dev.aegaeon.api.utils.LazyList;

/**
 * ClientDto.java
 * 
 * A Dto representing a client.
 *
 * @author by rguillemette
 * @since Nov 16, 2017
 */
public class ClientView {
    
    private Long id;
    
    private String publicId;
    
    private String name;
    
    private String description;
    
    private String providerType;
    
    private String secret;
    
    private String logoUrl;
    
    private Long idTokenSeconds;
    
    private Long accessTokenSeconds;
    
    private Long refreshTokenSeconds;
    
    private List<String> contacts = new LazyList<>();
    
    private List<String> redirections = new LazyList<>();
    
    private List<SelectableItemView> grants = new LazyList<>();
    
    private List<SelectableItemView> scopes = new LazyList<>();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param pId the id to set
     */
    public void setId(Long pId) {
        id = pId;
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
     * @return the providerType
     */
    public String getProviderType() {
        return providerType;
    }

    /**
     * @param pProviderType the providerType to set
     */
    public void setProviderType(String pProviderType) {
        providerType = pProviderType;
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
     * @return the contacts
     */
    public List<String> getContacts() {
        return contacts;
    }

    /**
     * @param pContacts the contacts to set
     */
    public void setContacts(List<String> pContacts) {
        contacts = pContacts;
    }

    /**
     * @param pIdx The index of the element to remove
     */
    public void removeContactAt(int pIdx) {
        if (contacts != null && contacts.size() > pIdx) {
            contacts.remove(pIdx);
        }
    }

    /**
     * @return the redirections
     */
    public List<String> getRedirections() {
        return redirections;
    }

    /**
     * @param pRedirections the redirections to set
     */
    public void setRedirections(List<String> pRedirections) {
        redirections = pRedirections;
    }

    /**
     * @param pIdx The index of the element to remove
     */
    public void removeRedirectionAt(int pIdx) {
        if (redirections != null && redirections.size() > pIdx) {
            redirections.remove(pIdx);
        }
    }


    /**
     * @return the grants
     */
    public List<SelectableItemView> getGrants() {
        return grants;
    }

    /**
     * @param pGrants the grants to set
     */
    public void setGrants(List<SelectableItemView> pGrants) {
        grants = pGrants;
    }

    /**
     * @return the scopes
     */
    public List<SelectableItemView> getScopes() {
        return scopes;
    }

    /**
     * @param pScopes the scopes to set
     */
    public void setScopes(List<SelectableItemView> pScopes) {
        scopes = pScopes;
    }
    
    
}
