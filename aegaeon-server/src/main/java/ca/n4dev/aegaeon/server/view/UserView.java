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

import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.utils.LazyList;

/**
 * UserView.java
 * 
 * A user view.
 *
 * @author by rguillemette
 * @since Dec 13, 2017
 */
public class UserView implements OAuthUser  {

    private Long id;

    private String userName;

    private String uniqueIdentifier;
    
    private String name;
    
    private List<String> roles;
    
    private String picture;
    
    private String locale;
    
    private List<UserInfoView> userInfos = new LazyList<>();

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
     * @return the roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * @param pRoles the roles to set
     */
    public void setRoles(List<String> pRoles) {
        roles = pRoles;
    }

    /**
     * @return the picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * @param pPicture the picture to set
     */
    public void setPicture(String pPicture) {
        picture = pPicture;
    }

    /**
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * @param pLocale the locale to set
     */
    public void setLocale(String pLocale) {
        locale = pLocale;
    }

    /**
     * @return the userInfos
     */
    public List<UserInfoView> getUserInfos() {
        return userInfos;
    }

    /**
     * @param pUserInfos the userInfos to set
     */
    public void setUserInfos(List<UserInfoView> pUserInfos) {
        userInfos = pUserInfos;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.OAuthUser#getId()
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.OAuthUser#getUniqueIdentifier()
     */
    @Override
    public String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param pUserName the userName
     */
    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    /**
     * @param pId the id to set
     */
    public void setId(Long pId) {
        id = pId;
    }

    /**
     * @param pUniqueIdentifier the uniqueIdentifier to set
     */
    public void setUniqueIdentifier(String pUniqueIdentifier) {
        uniqueIdentifier = pUniqueIdentifier;
    }

    public String toString() {
        return new StringBuilder()
                    .append(getClass().getSimpleName())
                    .append("[")
                    .append(this.name)
                    .append(",")
                    .append(this.uniqueIdentifier)
                    .append("]")
                    .toString();
    }
}
