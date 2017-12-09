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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.server.controller.dto.PageDto;
import ca.n4dev.aegaeon.server.view.ClientDto;
import ca.n4dev.aegaeon.server.view.mapper.ClientMapper;

/**
 * ClientService.java
 * 
 * Service managing client.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Service
public class ClientService extends SecuredBaseService<Client, ClientRepository> {

    private ClientMapper clientMapper;
    
    /**
     * Default Constructor.
     * @param pRepository Client repository.
     */
    @Autowired
    public ClientService(ClientRepository pRepository, 
                         ClientMapper pClientMapper) {
        super(pRepository);
        this.clientMapper = pClientMapper;
    }

    /**
     * 
     * @param pPageable
     * @return
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ClientDto> findByPage(Pageable pPageable) {
        Page<Client> page = getRepository().findAll(pPageable);
        
        List<ClientDto> dtos = page.getContent().stream()
                                        .map(c -> clientMapper.clientToClientDto(c))
                                        .collect(Collectors.toList());
        
        return new PageDto<>(dtos, pPageable, page.getTotalElements());
    }
    
    /**
     * Find a client by its public id. Usually, the public id 
     * is what is used duriong authorization.
     * @param pPublicId The client's public id.
     * @return A client or null.
     */
    @Transactional(readOnly = true)
    public Client findByPublicId(String pPublicId) {
        return this.getRepository().findByPublicId(pPublicId);
    }
    
    /**
     * Find a client by its public id. Usually, the public id 
     * is what is used duriong authorization.
     * @param pPublicId The client's public id.
     * @return A client or null.
     */
    @Transactional(readOnly = true)
    public Client findByPublicIdWithRedirections(String pPublicId) {
        return this.getRepository().findByPublicIdWithRedirections(pPublicId);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#findById(java.lang.Long)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ClientDto findOne(Long pId) {
        Client client = super.findById(pId);
        
        ClientDto clientDto = this.clientMapper.clientToClientDto(client);
        
        return clientDto;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#save(ca.n4dev.aegaeon.api.model.BaseEntity)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ClientDto save(ClientDto pEntity) {
        return pEntity;
//        Client client = this.clientDtoConverter.toEntity(pEntity);
//        return this.clientDtoConverter.toDto(super.save(client));
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#save(java.util.List)
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Client> save(List<Client> pEntities) {
        return super.save(pEntities);
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#delete(ca.n4dev.aegaeon.api.model.BaseEntity)
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Client pEntity) {
        super.delete(pEntity);
    }
    
}
