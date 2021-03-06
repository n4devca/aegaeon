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
import ca.n4dev.aegaeon.server.view.UserInfoView;

/**
 * UserInfoTypeGroupDto.java
 * 
 * Dto used by the ui layer to represent a group of UserTypeInfo entity.
 *
 * @author by rguillemette
 * @since Sep 8, 2017
 */
public class UserInfoGroupDto {

    private String code;
    
    private String labelName;
    
    private List<UserInfoView> children = new LazyList<>();
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param pCode the code to set
     */
    public void setCode(String pCode) {
        code = pCode;
    }

    /**
     * @return the labelName
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * @param pLabelName the labelName to set
     */
    public void setLabelName(String pLabelName) {
        labelName = pLabelName;
    }

    /**
     * @return the children
     */
    public List<UserInfoView> getChildren() {
        return children;
    }

    /**
     * @param pChildren the children to set
     */
    public void setChildren(List<UserInfoView> pChildren) {
        children = pChildren;
    }
    
    /**
     * Add one UserInfoTypeDto
     * @param pUserInfoTypeDto to add.
     */
    public void addUserInfoTypeDto(UserInfoView pUserInfoTypeDto) {
        if (pUserInfoTypeDto != null) {
            this.children.add(pUserInfoTypeDto);
        }
    }


    public String toString() {
        return new StringBuilder()
                    .append("UserInfoGroupDto{")
                    .append(this.code)
                    .append(", ").append(this.labelName)
                    .append(", children[").append(this.children.size())
                    .append("]}")
                    .toString();
    }
}
