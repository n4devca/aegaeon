/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.api.exception.OpenIdExceptionBuilder;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * OAuthPublicJwkController.java
 *
 * Expose public keys to clients.
 *
 * @author by rguillemette
 * @since Jul 31, 2017
 */
@Controller
@RequestMapping(value = PublicJwkController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "information", havingValue = "true", matchIfMissing = true)
public class PublicJwkController {

    public static final String URL = "/jwk";

    private TokenFactory tokenFactory;

    /**
     * Default Constructor.
     * @param pTokenFactory The token factory exposing the public keys.
     */
    @Autowired
    public PublicJwkController(TokenFactory pTokenFactory) {
        this.tokenFactory = pTokenFactory;
    }

    @RequestMapping(value = "",
                    method = {RequestMethod.GET, RequestMethod.POST},
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String jwk() {
        try {
            return this.tokenFactory.publicJwks();
        } catch (Exception pException) {
            throw new OpenIdExceptionBuilder(pException)
                    .code(ServerExceptionCode.UNEXPECTED_ERROR)
                    .build();
        }
    }
}
