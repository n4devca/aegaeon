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
package ca.n4dev.aegaeon.server.token.payload;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * SimplePayloadProvider.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 12, 2017
 */
@Service
public class SimplePayloadProvider implements PayloadProvider {
    
    private UserService userService;
    
    @Autowired
    public SimplePayloadProvider(UserService pUserService) {
        this.userService = pUserService;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.payload.PayloadProvider
     * #createPayload(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.util.List)
     */
    @Override
    public Map<String, String> createPayload(OAuthUser pOAuthUser, OAuthClient pOAuthClient, List<String> pRequestedScopes) {
        Map<String, String> payload = new LinkedHashMap<>();
        
        if (Utils.contains(pRequestedScopes, "profile")) {
            
            //User user = this.userService.findById(pOAuthUser.getId());
            payload.put("email", pOAuthUser.getEmail());
        }

        return Collections.unmodifiableMap(payload);
    }

}
