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

import java.util.LinkedHashMap;
import java.util.Map;

import ca.n4dev.aegaeon.api.exception.OpenIdErrorType;
import ca.n4dev.aegaeon.api.exception.OpenIdException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import ca.n4dev.aegaeon.server.view.TokenResponse;

/**
 * UriBuilder.java
 * 
 * Useful static functions to deal with url building.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
public class UriBuilder {

    public static final String REDIRECTION_ERROR_KEY = "error";
    public static final String REDIRECTION_DESC_KEY = "error_description";

    public static final String PARAM_PROMPT = "prompt";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_REDIRECTION_URL = "redirection_url";
    public static final String PARAM_RESPONSE_TYPE = "response_type";
    public static final String PARAM_NONCE = "nonce";

    public static String build(String pUrl, TokenResponse pTokenResponse, String pState) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        
        append(params, "access_token", pTokenResponse.getAccessToken());
        append(params, "refresh_token", pTokenResponse.getRefreshToken());
        append(params, "id_token", pTokenResponse.getIdToken());
        
        append(params, "token_type", pTokenResponse.getTokenType());
        append(params, "expires_in", pTokenResponse.getExpiresIn());
        append(params, "scope", pTokenResponse.getScope());
        append(params, "state", pState);

        return build(pUrl, params, false);
    }

    public static String build(String pUrl, OpenIdException pOpenIdException, boolean pAsFragment) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(buildModel(pOpenIdException));
        return build(pUrl, params, pAsFragment);
    }

    public static String build(String pUrl, MultiValueMap<String, String> pParam, boolean pAsFragment) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(pUrl);

        if (pAsFragment) {
            String queryParams = UriComponentsBuilder.fromHttpUrl(pUrl).queryParams(pParam).build().getQuery();
            builder = builder.fragment(queryParams);
        } else {
            builder = builder.queryParams(pParam);
        }
        UriComponents uriComponents =  builder.build();
        return uriComponents.toUri().toString();
    }

    public static Map<String, String> buildModel(OpenIdException pOpenIdException) {
        LinkedHashMap<String, String> model = new LinkedHashMap<>();

        put(model, REDIRECTION_ERROR_KEY, OpenIdErrorType.fromServerCode(pOpenIdException.getCode()).toString());
        put(model, REDIRECTION_DESC_KEY, pOpenIdException.getMessage());
        put(model, PARAM_STATE, pOpenIdException.getClientState());

        return model;
    }
    
    private static void append(MultiValueMap<String, String> pParams, String pKey, String pValue) {
        if (Utils.isNotEmpty(pKey) && Utils.isNotEmpty(pValue)) {
            pParams.add(pKey, pValue);
        }
    }

    private static void put(Map<String, String> pParams, String pKey, String pValue) {
        if (Utils.isNotEmpty(pKey) && Utils.isNotEmpty(pValue)) {
            pParams.put(pKey, pValue);
        }
    }

}
