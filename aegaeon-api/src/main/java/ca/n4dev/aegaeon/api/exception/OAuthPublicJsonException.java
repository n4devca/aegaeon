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
package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.AuthorizationGrant;

/**
 * OAuthPublicJsonException.java
 * 
 * OAuth error throwed by the token or authorize endpoint.
 * Public and mostly shown to client by returning http400 + json.
 * 
 * https://tools.ietf.org/html/rfc6749#section-4.2.2.1
 *
 * @author by rguillemette
 * @since May 29, 2017
 */
public class OAuthPublicJsonException extends BaseOAuthException {

    private static final long serialVersionUID = -3097198698305780764L;

    /**
     * @param pGrantType
     * @param pError
     */
    public OAuthPublicJsonException(AuthorizationGrant pGrantType, OAuthErrorType pError) {
        super(pGrantType, pError);
    }

}
