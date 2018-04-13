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
package ca.n4dev.aegaeon.api.protocol;

/**
 * GrantType.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 24, 2017
 */
public enum GrantType {
    AUTHORIZATION_CODE,
    IMPLICIT,
    CLIENT_CREDENTIALS,
    REFRESH_TOKEN,
    PASSWORD, // Not implemented
    HYBRID
    ;
    
    public static GrantType from(String pGrantString) {
        for (GrantType grant : GrantType.values()) {
            if (grant.toString().equals(pGrantString)) {
                return grant;
            }
        }
        
        return null;
    }
    
    public static boolean is(String pParameter, GrantType pGrant) {
        if (pGrant != null && pParameter != null && !pParameter.isEmpty()) {
            return pGrant == from(pParameter);            
        }
        
        return false;
    }

}
