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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ClientType.java
 * 
 * Represent the type of client :
 *  - Implicit
 *  - Authorization code
 *  - ...
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Entity
@Table(name = "client_type")
public class ClientType extends BaseEntity {

    public static final String CODE_AUTH_CODE = "AUTH_CODE";
    public static final String CODE_IMPLICIT = "IMPLICIT";
    public static final String CODE_CLIENT_CREDENTIALS = "CLIENT_CREDENTIALS";
    
    private String code;

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
    
    public boolean is(String pCode) {
        return this.code.equals(pCode);
    }
}
