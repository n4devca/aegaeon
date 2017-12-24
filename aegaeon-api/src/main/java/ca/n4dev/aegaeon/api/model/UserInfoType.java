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

package ca.n4dev.aegaeon.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author by rguillemette
 * @since Sep 4, 2017
 *
 */
@Entity
@Table(name = "user_info_type")
public class UserInfoType extends BaseEntity {

	private String code;
	
	@Column(name = "parent_id", insertable = false, updatable = false)
	private Long parentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private UserInfoType parent;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the parent
	 */
	public UserInfoType getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(UserInfoType parent) {
		this.parent = parent;
	}

	/**
	 * @return the parentId
	 */
	public Long getParentId() {
		return parentId;
	}
	
	public boolean isOther() {
	    return this.code.contains("OTHER");
	}
}
