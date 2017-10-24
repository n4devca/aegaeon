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
import java.util.LinkedHashMap;
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
	
	private Map<String, UserInfoGroupDto> groups = new LinkedHashMap<>();
	
	public UserFormDto() {
	    
	}
	
	public UserFormDto(String pName, String pPictureUrl, Map<Long, UserInfoGroupDto> pGroups, List<UserInfoDto> pUserValues) {
	    this.name = pName;
	    this.pictureUrl = pPictureUrl;

	    // Set Groups
	    for (Entry<Long, UserInfoGroupDto> en : pGroups.entrySet()) {
	        UserInfoGroupDto g = en.getValue();
	        groups.put(g.getCode(), g);
	    }
	    
	    // Distribute values
	    for (UserInfoDto udto : pUserValues) {
	        groups.get(udto.getParentCode()).addValue(udto);
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
     * @return the groups
     */
    public Map<String, UserInfoGroupDto> getGroups() {
        return groups;
    }

    /**
     * @param pGroups the groups to set
     */
    public void setGroups(Map<String, UserInfoGroupDto> pGroups) {
        groups = pGroups;
    }

}
