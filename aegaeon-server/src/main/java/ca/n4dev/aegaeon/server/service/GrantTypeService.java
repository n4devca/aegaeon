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

import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.api.repository.GrantTypeRepository;

/**
 * GrantService.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Nov 23, 2017
 */
@Service
public class GrantTypeService  extends BaseService<GrantType, GrantTypeRepository>{

    /**
     * Default Constructor.
     * @param pRepository GrantType repository.
     */
    @Autowired
    public GrantTypeService(GrantTypeRepository pRepository) {
        super(pRepository);
    }


    /**
     * Find all grants.
     * @return A list of grants.
     */
    @Transactional(readOnly = true)
    public List<GrantType> findAll() {
        return this.getRepository().findAll();
    }

}
