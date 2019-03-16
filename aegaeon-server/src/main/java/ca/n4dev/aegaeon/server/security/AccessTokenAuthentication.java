/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.server.view.ScopeView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * AccessTokenAuthentication.java
 *
 * An authentication by token.
 *
 * @author by rguillemette
 * @since Jul 18, 2017
 */
public class AccessTokenAuthentication implements Authentication {

    private static final long serialVersionUID = 5436208889960585525L;

    private Long userId;
    private String uniqueIdentifier;
    private String accessToken;
    private Set<ScopeView> scopes;
    private boolean authenticated = false;
    private Collection<? extends GrantedAuthority> authorities;

    public AccessTokenAuthentication(String pAccessToken) {
        this.accessToken = pAccessToken;
    }

    public AccessTokenAuthentication(OAuthUser pOAuthUser, String pAccessToken, Set<ScopeView> pScopes, List<String> pRoles) {
        this.userId = pOAuthUser.getId();
        this.uniqueIdentifier = pOAuthUser.getUniqueIdentifier();
        this.scopes = pScopes;
        this.accessToken = pAccessToken;

        if (pRoles != null) {
            List<GrantedAuthority> roles = new ArrayList<>();

            for (String role : pRoles) {
                roles.add(new SimpleGrantedAuthority(role));
            }

            this.authorities = roles;
        }
    }

    /* (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    @Override
    public String getName() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getDetails()
     */
    @Override
    public Object getDetails() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
        return this.accessToken;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
     */
    @Override
    public void setAuthenticated(boolean pIsAuthenticated) throws IllegalArgumentException {
        this.authenticated = pIsAuthenticated;
    }

    /**
     * @return the accessToken
     */
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
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return the scopes
     */
    public Set<ScopeView> getScopes() {
        return scopes;
    }

    /**
     * @return the uniqueIdentifier
     */
    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

}
