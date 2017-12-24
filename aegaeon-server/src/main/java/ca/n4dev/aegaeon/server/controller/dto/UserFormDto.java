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

import java.util.List;

import ca.n4dev.aegaeon.api.utils.LazyList;
import ca.n4dev.aegaeon.server.view.UserView;

/**
 *
 * @author by rguillemette
 * @since Sep 17, 2017
 *
 */
public class UserFormDto {
    
    private UserView userView;
    
    private List<UserInfoGroupDto> groupInfo = new LazyList<>();

    /**
     * @return the userView
     */
    public UserView getUserView() {
        return userView;
    }

    /**
     * @param pUserView the userView to set
     */
    public void setUserView(UserView pUserView) {
        userView = pUserView;
    }

    /**
     * @return the groupInfo
     */
    public List<UserInfoGroupDto> getGroupInfo() {
        return groupInfo;
    }

    /**
     * @param pGroupInfo the groupInfo to set
     */
    public void setGroupInfo(List<UserInfoGroupDto> pGroupInfo) {
        groupInfo = pGroupInfo;
    }

    /**
     * @param pGroupInfo the groupInfo to set
     */
    public void addGroupInfo(UserInfoGroupDto pGroupInfo) {
        groupInfo.add(pGroupInfo);
    }
}
