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

/**
 * UserInfoTypeDto.java
 * 
 * Dto used by the ui layer to represent a UserTypeInfo entity.
 *
 * @author by rguillemette
 * @since Sep 8, 2017
 */
public class UserInfoDto {

    private String code;
    
    private String labelName;    
    
    private String value;
    
    private Long refId;
    
    private String parentCode;
    
    public UserInfoDto() {}
    
    /**
     * 
     * @param pCode
     * @param pLabelName
     */
    public UserInfoDto(Long pRefId, String pCode, String pLabelName) {
        this(pRefId, pCode, pLabelName, null);
    }

    public UserInfoDto(Long pRefId, String pCode, String pLabelName, String pValue) {
        this.refId = pRefId;
        this.code = pCode;
        this.labelName = pLabelName;
        this.value = pValue;
    }

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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param pValue the value to set
     */
    public void setValue(String pValue) {
        value = pValue;
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
     * @return the refId
     */
    public Long getRefId() {
        return refId;
    }

    /**
     * @param pRefId the refId to set
     */
    public void setRefId(Long pRefId) {
        refId = pRefId;
    }

    /**
     * @return the parentCode
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * @param pParentCode the parentCode to set
     */
    public void setParentCode(String pParentCode) {
        parentCode = pParentCode;
    }
    
}
