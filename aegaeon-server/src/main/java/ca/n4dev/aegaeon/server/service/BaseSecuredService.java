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

import ca.n4dev.aegaeon.api.model.BaseEntity;

/**
 * SecuredBaseService.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 7, 2017
 */
public abstract class BaseSecuredService<E extends BaseEntity, R extends JpaRepository<E, Long>> {

    private R repository;
    
    protected BaseSecuredService(R pRepository) {
        this.repository = pRepository;
    }
    
    protected E findById(Long pId) {
        return getRepository().findOne(pId);
    }
    
    protected E save(E pEntity) {
        return getRepository().save(pEntity);
    }
    
    protected List<E> save(List<E> pEntities) {
        return getRepository().save(pEntities);
    }
    
    protected void delete(Long pEntityId) {
        getRepository().delete(pEntityId);
    }
    
    protected R getRepository() {
        return this.repository;
    }
}
