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
package ca.n4dev.aegaeon.server.utils;

import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.model.ClientScope;
import ca.n4dev.aegaeon.server.model.GrantType;

/**
 * ClientUtils.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 21, 2017
 */
public class ClientUtils {

    private ClientUtils() {}

    public static boolean hasClientScope(Client pClient, String pScopeName) {
        if (pClient != null && Utils.isNotEmpty(pScopeName)) {
            for (ClientScope sc : pClient.getScopes()) {
                if (sc.getScope().getName().equals(pScopeName)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean hasClientGrant(Client pClient, String pGrantType) {
        if (pClient != null && Utils.isNotEmpty(pGrantType)) {
            for (GrantType g : pClient.getGrantTypes()) {
                if (g.getCode().equals(pGrantType)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
}
