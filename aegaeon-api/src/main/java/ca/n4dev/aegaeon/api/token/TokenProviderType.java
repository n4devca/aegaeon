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
package ca.n4dev.aegaeon.api.token;

/**
 * TokenProviderType.java
 * 
 * Type of Token a provider is creating.
 *
 * @author by rguillemette
 * @since May 14, 2017
 */
public enum TokenProviderType {

    HMAC_HS256("HMAC with SHA-256"),
    HMAC_HS512("HMAC with SHA-512"),
    RSA_RS256("RSA PKCS#1 with SHA-256"),
    RSA_RS512("RSA PKCS#1 with SHA-512"),
    UUID("Simple UUID (no signature)")
    ;
    
    String typeName;
    
    TokenProviderType(String pName) {
        this.typeName = pName;
    }
    
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public static TokenProviderType from(String pName) {
        for (TokenProviderType t : TokenProviderType.values()) {
            if (t.toString().equals(pName)) {
                return t;
            }
        }
        return null;
    }
}
