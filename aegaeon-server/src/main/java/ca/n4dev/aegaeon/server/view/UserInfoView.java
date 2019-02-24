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

/**
 * UserInfoView.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 13, 2017
 */
public class UserInfoView {

    private int index;

    private Long refId;
    
    private Long refTypeId;
    
    private String category;
    
    private String code;
    
    private String name;
    
    private String value;
    
    public UserInfoView(){}
    
    public UserInfoView(Long pId, String pCode, String pName, String pCategory, String pValue){
        this.refId = pId;
        this.code = pCode;
        this.name = pName;
        this.category = pCategory;
        this.value = pValue;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param pCategory the category to set
     */
    public void setCategory(String pCategory) {
        category = pCategory;
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
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param pIndex the index to set
     */
    public void setIndex(int pIndex) {
        index = pIndex;
    }

    /**
     * @return the otherType
     */
    public boolean isOtherType() {
        return code != null && code.contains("OTHER");
    }

    /**
     * @return the refTypeId
     */
    public Long getRefTypeId() {
        return refTypeId;
    }

    /**
     * @param pRefTypeId the refTypeId to set
     */
    public void setRefTypeId(Long pRefTypeId) {
        refTypeId = pRefTypeId;
    }
    
    public String toString() {
        return new StringBuilder()
                    .append(getClass().getSimpleName())
                    .append("[refId:").append(this.refId)
                    .append(",code:").append(this.code)
                    .append(",value:").append(this.value)
                    .append("]")
                    .toString();
    }
}
