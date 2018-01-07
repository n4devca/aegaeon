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
 * AuthorizationCodeView.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 10, 2017
 */

public class AuthorizationCodeView {

    private String code;
    
    private String clientId;
    
    private String userName;

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
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param pClientId the clientId to set
     */
    public void setClientId(String pClientId) {
        clientId = pClientId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param pUserName the userName to set
     */
    public void setUserName(String pUserName) {
        userName = pUserName;
    }

}
