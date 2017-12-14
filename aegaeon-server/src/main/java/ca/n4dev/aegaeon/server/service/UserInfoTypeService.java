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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.model.UserInfoType;
import ca.n4dev.aegaeon.api.repository.UserInfoTypeRepository;

/**
 *
 * @author by rguillemette
 * @since Sep 4, 2017
 *
 */
@Service
public class UserInfoTypeService extends BaseSecuredService<UserInfoType, UserInfoTypeRepository> {

	/**
	 * @param pRepository
	 */
	@Autowired
	public UserInfoTypeService(UserInfoTypeRepository pRepository) {
		super(pRepository);
	}

	/**
	 * Find all user info type.
	 * @return All entities.
	 */
	@Transactional(readOnly = true)
	public List<UserInfoType> findAll() {
		return getRepository().findAll();
	}
}
