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
 * OauthRestrictedException.java
 * 
 * OAuth error throwed by the token or authorize endpoint.
 * These are restricted and should be hidden from the client.
 * 
 * https://tools.ietf.org/html/rfc6749#section-4.2.2.1
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public class OauthRestrictedException extends BaseOAuthException {

    private static final long serialVersionUID = 5891552760445577562L;

    private String clientPublicId;
    
    /**
     * Construct a restricted error.
     * @param pGrantType The grant type of this oauth request.
     * @param pError The error code.
     * @param pClientPublicId The client public id.
     * @param pRedirectUrl The redirection url.
     */
    public OauthRestrictedException(Class<?> pSource, AuthorizationGrant pGrantType, OAuthErrorType pError, String pClientPublicId, String pRedirectUrl) {
        this(pSource, pGrantType, pError, pClientPublicId, pRedirectUrl, null);
    }
    

    /**
     * Construct a restricted error.
     * @param pGrantType The grant type of this oauth request.
     * @param pError The error code.
     * @param pClientPublicId The client public id.
     * @param pRedirectUrl The redirection url.
     * @param pMessage A message or error description.
     */
    public OauthRestrictedException(Class<?> pSource, AuthorizationGrant pGrantType, OAuthErrorType pError, String pClientPublicId, String pRedirectUrl, String pMessage) {
        super(pSource, pGrantType, pError);
        setRedirectUrl(pRedirectUrl);
        this.clientPublicId = pClientPublicId;
        setErrorDescription(pMessage);
    }

    /**
     * @return the clientPublicId
     */
    public String getClientPublicId() {
        return clientPublicId;
    }

    /**
     * @param pClientPublicId the clientPublicId to set
     */
    public void setClientPublicId(String pClientPublicId) {
        clientPublicId = pClientPublicId;
    }
}
