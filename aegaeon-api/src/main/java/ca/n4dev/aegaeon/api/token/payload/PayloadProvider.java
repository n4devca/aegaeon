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
package ca.n4dev.aegaeon.api.token.payload;

import java.util.Map;
import java.util.Set;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;

/**
 * PayloadProvider.java
 * 
 * A provider able to translate the result of an authentication
 * into a payload, ie. a set of user's information.
 *
 * @author by rguillemette
 * @since Jul 12, 2017
 */
public interface PayloadProvider {

    /**
     * Create a Payload (some user's info) to include in a JWT token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client used during authentication.
     * @param pRequestedScopes The requested scopes.
     * @return A payload as a Map (k=>v).
     */
    Map<String, String> createPayload(OAuthUser pOAuthUser,
                                      OAuthClient pOAuthClient,
                                      Set<String> pRequestedScopes);
}
