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
package ca.n4dev.aegaeon.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.n4dev.aegaeon.api.model.RefreshToken;

/**
 * RefreshTokenRepository.java
 * 
 * Repository managing refresh token entity.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find a refresh token using its value and client to whom it has been granted.
     * @param pToken The token value.
     * @param pClientId The client primary key.
     * @return A RefreshToken or null.
     */
    RefreshToken findByTokenAndClientId(String pToken, Long pClientId);
}
