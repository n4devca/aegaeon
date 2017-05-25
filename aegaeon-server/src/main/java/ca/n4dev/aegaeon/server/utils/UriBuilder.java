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

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import ca.n4dev.aegaeon.server.controller.dto.TokenResponse;
import ca.n4dev.aegaeon.server.exception.OAuthPublicException;

/**
 * UriBuilder.java
 * 
 * Useful static functions to deal with url building.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
public class UriBuilder {

    public static String build(String pUrl, TokenResponse pTokenResponse, String pState) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        
        append(params, "access_token", pTokenResponse.getAccessToken());
        append(params, "token_type", pTokenResponse.getTokenType());
        append(params, "expires_in", pTokenResponse.getExpiresIn());
        append(params, "refresh_token", pTokenResponse.getRefreshToken());
        append(params, "scope", pTokenResponse.getScopeList());
        append(params, "state", pState);

        return build(pUrl, params);
    }
    
    public static String build(String pUrl, OAuthPublicException pOAuthPublicException) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        
        append(params, "state", pOAuthPublicException.getState());
        append(params, "error", pOAuthPublicException.getError().toString());
        
        return build(pUrl, params);
    }
    
    public static String build(String pUrl, MultiValueMap<String, String> pParam) {
        UriComponents uriComponents =  UriComponentsBuilder.fromHttpUrl(pUrl).queryParams(pParam).build();
        return uriComponents.toUri().toString();
    }
    
    private static void append(MultiValueMap<String, String> pParams, String pKey, String pValue) {
        if (Utils.isNotEmpty(pKey) && Utils.isNotEmpty(pValue)) {
            pParams.add(pKey, pValue);
        }
    }
}
