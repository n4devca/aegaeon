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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.api.repository.UserInfoRepository;

/**
 *
 * @author by rguillemette
 * @since Sep 4, 2017
 *
 */
@Service
public class UserInfoService extends BaseSecuredService<UserInfo, UserInfoRepository> {

	/**
	 * @param pRepository
	 */
	@Autowired
	public UserInfoService(UserInfoRepository pRepository) {
		super(pRepository);
	}

	/**
	 * Find all user info by user id.
	 * @param pUserId A user's id.
	 * @return A list of user info.
	 */
	@Transactional(readOnly = true)
    @PreAuthorize("hasRole('CLIENT') or principal.id == #pUserId")
	public List<UserInfo> findByUserId(Long pUserId) {
		return getRepository().findByUserId(pUserId);
	}
}
