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
 * ScopeDto.java
 * 
 * A dto for scope.
 *
 * @author by rguillemette
 * @since Nov 22, 2017
 */
public class SelectableItemView {

    private Long id;
    
    private String name;
    
    private String description;
    
    private boolean selected;

    public SelectableItemView() {}
    
    /**
     * Create a complete scopeDto.
     * @param pId The scope id
     * @param pName The scope name
     * @param pDescription A default description
     */
    public SelectableItemView(Long pId, String pName, String pDescription, boolean pSelected) {
        this.id= pId;
        this.name = pName;
        this.description = pDescription;
        this.selected = pSelected;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param pDescription the description to set
     */
    public void setDescription(String pDescription) {
        description = pDescription;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param pId the id to set
     */
    public void setId(Long pId) {
        id = pId;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param pSelected the selected to set
     */
    public void setSelected(boolean pSelected) {
        selected = pSelected;
    }
    
    
}
