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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.server.model.UserAuthorization;
import ca.n4dev.aegaeon.server.repository.UserAuthorizationRepository;

/**
 * UserAuthorizationService.java
 * 
 * Manage {@link UserAuthorization} entity.
 *
 * @author by rguillemette
 * @since May 13, 2017
 */
@Service
public class UserAuthorizationService extends BaseService<UserAuthorization, UserAuthorizationRepository> {

    /**
     * @param pRepository
     */
    @Autowired
    public UserAuthorizationService(UserAuthorizationRepository pRepository) {
        super(pRepository);
    }

    /**
     * Find a UserAuthorization by user and client.
     * @param pUserId The user's id.
     * @param pClientId The client's id.
     * @return A UserAuthorization or null.
     */
    @Transactional(readOnly = true)
    public UserAuthorization findByUserIdAndClientId(Long pUserId, Long pClientId) {
        return this.getRepository().findByUserIdAndClientId(pUserId, pClientId);
    }
}
