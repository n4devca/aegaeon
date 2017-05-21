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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import ca.n4dev.aegaeon.api.token.OAuthUser;


/**
 * User.java
 * 
 * User entity.
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity implements OAuthUser {

    private String userName;
    
    private String passwd;
    
    private String email;

    @Column(name = "uniqueIdentifier")
    private String uniqueIdentifier;
    
    private String name;
    
    @Type(type = "boolean")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="authority_id", referencedColumnName="id"))
    private List<Authority> authorities;
    
    
    public User() {}
    
    public User(Long pId) {
        this.setId(pId);
    }
    
    
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param pUserName the userName to set
     */
    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param pEnabled the enabled to set
     */
    public void setEnabled(boolean pEnabled) {
        enabled = pEnabled;
    }

    /**
     * @return the authorities
     */
    public List<Authority> getAuthorities() {
        return authorities;
    }

    /**
     * @param pAuthorities the authorities to set
     */
    public void setAuthorities(List<Authority> pAuthorities) {
        authorities = pAuthorities;
    }

    /**
     * @return the passwd
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * @param pPasswd the passwd to set
     */
    public void setPasswd(String pPasswd) {
        passwd = pPasswd;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.OAuthUser#getSub()
     */
    @Override
    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.OAuthUser#getEmail()
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * @param pEmail the email to set
     */
    public void setEmail(String pEmail) {
        email = pEmail;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.OAuthUser#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param pName the name to set
     */
    public void setName(String pName) {
        name = pName;
    }
    
}
