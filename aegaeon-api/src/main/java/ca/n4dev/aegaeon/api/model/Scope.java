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
package ca.n4dev.aegaeon.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Scope.java
 * 
 * Represent a scope that can be requested by a client.
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Entity
@Table(name = "scope")
public class Scope extends BaseEntity {

    private String name;
    
    private String description;
    
    @Column(name = "issystem")
    @Type(type = "boolean")
    private boolean system;

    @Column(name = "defaultvalue")
    @Type(type = "boolean")
    private boolean defaultValue;

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
     * @return the system
     */
    public boolean isSystem() {
        return system;
    }

    /**
     * @param pSystem the system to set
     */
    public void setSystem(boolean pSystem) {
        system = pSystem;
    }

    /**
     *
     * @return
     */
    public boolean isDefaultValue() {
        return defaultValue;
    }

}
