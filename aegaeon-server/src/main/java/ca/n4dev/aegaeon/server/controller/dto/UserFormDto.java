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

package ca.n4dev.aegaeon.server.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author by rguillemette
 * @since Sep 17, 2017
 *
 */
public class UserFormDto {
    

	private String pictureUrl;
	
	private String name;
	
    private List<UserInfoDto> userValues = new ArrayList<>();
	
	private UserInfoGroupDto phones;

	private UserInfoGroupDto emails;
	
	private UserInfoGroupDto socialMedias;
	
	private UserInfoGroupDto addresses;
	
	private UserInfoGroupDto personals;
	
	public UserFormDto() {
	}
	
	public UserFormDto(String pName, String pPictureUrl, Map<Long, UserInfoGroupDto> pGroups, List<UserInfoDto> pUserValues) {
	    this.name = pName;
	    this.pictureUrl = pPictureUrl;
	    this.userValues = pUserValues;
	    
	    for (Entry<Long, UserInfoGroupDto> en : pGroups.entrySet()) {
	        
	        UserInfoGroupDto g = en.getValue();
	        
	        if ("PHONE".equals(g.getCode())) {
	            this.phones = g;
	        } else if ("SOCIALMEDIA".equals(g.getCode())) {
                this.socialMedias = g;
            } else if ("ADDRESS".equals(g.getCode())) {
                this.addresses = g;
            } else if ("PERSONAL".equals(g.getCode())) {
                this.personals = g;
            } else if ("EMAIL".equals(g.getCode())) {
                this.emails = g;
            }
	    }
	}

	/**
	 * @return the pictureUrl
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}

	/**
	 * @param pictureUrl the pictureUrl to set
	 */
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * @return the phones
     */
    public UserInfoGroupDto getPhones() {
        return phones;
    }

    /**
     * @param pPhones the phones to set
     */
    public void setPhones(UserInfoGroupDto pPhones) {
        phones = pPhones;
    }

    /**
     * @return the socialMedias
     */
    public UserInfoGroupDto getSocialMedias() {
        return socialMedias;
    }

    /**
     * @param pSocialMedias the socialMedias to set
     */
    public void setSocialMedias(UserInfoGroupDto pSocialMedias) {
        socialMedias = pSocialMedias;
    }

    /**
     * @return the addresses
     */
    public UserInfoGroupDto getAddresses() {
        return addresses;
    }

    /**
     * @param pAddresses the addresses to set
     */
    public void setAddresses(UserInfoGroupDto pAddresses) {
        addresses = pAddresses;
    }

    /**
     * @return the emails
     */
    public UserInfoGroupDto getEmails() {
        return emails;
    }

    /**
     * @param pEmails the emails to set
     */
    public void setEmails(UserInfoGroupDto pEmails) {
        emails = pEmails;
    }

    /**
     * @return the personals
     */
    public UserInfoGroupDto getPersonals() {
        return personals;
    }

    /**
     * @param pPersonals the personals to set
     */
    public void setPersonals(UserInfoGroupDto pPersonals) {
        personals = pPersonals;
    }


    /**
     * @param pValues the values to add
     */
    public void addUserValue(UserInfoDto pValue) {
        userValues.add(pValue);
    }

    /**
     * @return the userValues
     */
    public List<UserInfoDto> getUserValues() {
        return userValues;
    }

    /**
     * @param pUserValues the userValues to set
     */
    public void setUserValues(List<UserInfoDto> pUserValues) {
        userValues = pUserValues;
    }
}
