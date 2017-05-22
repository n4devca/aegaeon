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
package ca.n4dev.aegaeon.server.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ca.n4dev.aegaeon.server.model.AuthorizationCode;
import ca.n4dev.aegaeon.server.model.Client;
import ca.n4dev.aegaeon.server.repository.AuthorizationCodeRepository;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.token.TokenFactory;

/**
 * AuthorizationCodeService.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 10, 2017
 */
@Service
public class AuthorizationCodeService extends BaseService<AuthorizationCode, AuthorizationCodeRepository> {

    private TokenFactory tokenFactory;
    
    /**
     * Default Constructor.
     * @param pRepository Service repository.
     */
    @Autowired
    public AuthorizationCodeService(AuthorizationCodeRepository pRepository, TokenFactory pTokenFactory) {
        super(pRepository);
        
        this.tokenFactory = pTokenFactory;
    }

    /**
     * Find a AuthCode by code (string)
     * @param pCode The code.
     * @return An {@link AuthorizationCode} or null.
     */
    @Transactional(readOnly = true)
    public AuthorizationCode findByCode(String pCode) {
        return this.getRepository().findByCode(pCode);
    }
    
    /**
     * Create an {@link AuthorizationCode} for a user and a client.
     * @param pUser The user.
     * @param pClient The client.
     * @return A code or null.
     */
    @Transactional
    public AuthorizationCode createCode(SpringAuthUserDetails pUser, Client pClient) {
        Assert.notNull(pUser, "A code cannot be created without a user");
        Assert.notNull(pClient, "A code cannot be created without a client");

        AuthorizationCode c = new AuthorizationCode();
        c.setClient(pClient);
        c.setUser(pUser.asUser());
        c.setCode(this.tokenFactory.uniqueCode());
        c.setValidUntil(LocalDateTime.now().plus(3L, ChronoUnit.MINUTES));

        return this.getRepository().save(c);
    }
}
