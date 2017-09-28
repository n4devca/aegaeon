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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.model.BaseEntity;

/**
 * BaseService.java
 * 
 * Basic service with common functions.
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Transactional
public abstract class BaseService<E extends BaseEntity, R extends JpaRepository<E, Long>> {

    private R repository;
    
    protected BaseService(R pRepository) {
        this.repository = pRepository;
    }
    
    @Transactional(readOnly = true)
    public E findById(Long pId) {
        return getRepository().findOne(pId);
    }
    
    @Transactional
    public E save(E pEntity) {
        return getRepository().save(pEntity);
    }
    
    @Transactional
    public List<E> save(List<E> pEntities) {
        return getRepository().save(pEntities);
    }
    
    @Transactional
    public void delete(E pEntity) {
        getRepository().delete(pEntity);
    }
    
    protected R getRepository() {
        return this.repository;
    }
}
