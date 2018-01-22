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
package ca.n4dev.aegaeon.server.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ca.n4dev.aegaeon.api.model.User;

/**
 * SimpleUserDetails.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 13, 2017
 */
public class SpringAuthUserDetails implements UserDetails {

    private static final long serialVersionUID = -5669423040345933280L;
    private Long id;
    private String username;
    private String password;
    private boolean enable;
    private boolean nonlocked;
    private Collection<? extends GrantedAuthority> authorities;
    /**
     * Default Contructor.
     * @param pUsername The user's username.
     * @param pPassword the user's password.
     * @param pEnable If this user is enabled.
     * @param pNonLocked If this user is not locked.
     * @param pAuthorities The collection of authorities of this user.
     */
    public SpringAuthUserDetails(Long pId,
                             String pUsername, 
                             String pPassword, 
                             boolean pEnable, 
                             boolean pNonLocked, 
                             Collection<? extends GrantedAuthority> pAuthorities) {
        this.id = pId;
        this.username = pUsername;
        this.password = pPassword;
        this.enable = pEnable;
        this.nonlocked = pNonLocked;
        this.authorities = pAuthorities;
    }
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUserName()
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.nonlocked;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return this.enable;
    }

    /**
     * @return A user entity by simply setting the user's id.
     */
    public User asUser() {
        return new User(id);
    }
    
    public String toString() {
        return new StringBuilder()
                        .append(id)
                        .append(",")
                        .append(username)
                        .append(",")
                        .append(this.enable)
                        .append(",")
                        .append(this.nonlocked)
                        .toString();
    }
}
