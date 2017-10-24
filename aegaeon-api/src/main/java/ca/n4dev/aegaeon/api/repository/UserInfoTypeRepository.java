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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.n4dev.aegaeon.api.model.UserInfoType;

/**
 * UserInfoTypeRepository
 * 
 * Repository managing UserInfoType.
 * 
 * @author by rguillemette
 * @since Sep 4, 2017
 *
 */
public interface UserInfoTypeRepository extends JpaRepository<UserInfoType, Long> {

	/**
	 * Find user info type by code.
	 * @param pCode The code to search.
	 * @return A userinfotype or null.
	 */
	UserInfoType findByCode(String pCode);
	
	/**
	 * Find all user info type having a prent.
	 * @param pParentId The parent's id.
	 * @return A list of user info type.
	 */
	List<UserInfoType> findByParentId(Long pParentId);
	
}
