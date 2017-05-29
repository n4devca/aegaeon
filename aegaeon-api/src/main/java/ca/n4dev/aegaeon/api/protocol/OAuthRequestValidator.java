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

import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;

/**
 * OAuthRequestValidator.java
 * 
 * An interface describing a validation done on an oauth request.
 *
 * @author by rguillemette
 * @since May 26, 2017
 */
public interface OAuthRequestValidator {

    /**
     * Validate an OAuth request.
     * 
     * This function check if the parameters are following OAuth RFC as defined here :
     * https://tools.ietf.org/html/rfc6749#section-4.1.2.1
     * 
     * @param pResponseType The expected response type.
     * @param pClientPublicId The client public id as string.
     * @param pScope The requested scope.
     * @param pRedirectionUrl The requested redirect url.
     * @param pState The client state.
     * @throws OauthRestrictedException When a condition does not validate and must not be communicated to the client.
     * @throws OAuthPublicRedirectionException When a condition does not validate and must be communicated to the client (using redirection).
     */
    void validate(String pResponseType,
                     String pClientPublicId,
                     String[] pScope,
                     String pRedirectionUrl,
                     String pState) throws OauthRestrictedException, OAuthPublicRedirectionException;
}
