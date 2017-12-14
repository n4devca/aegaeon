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
import ca.n4dev.aegaeon.api.model.ClientContact;
import ca.n4dev.aegaeon.api.model.ClientGrantType;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.api.repository.ClientContactRepository;
import ca.n4dev.aegaeon.api.repository.ClientGrantTypeRepository;
import ca.n4dev.aegaeon.api.repository.ClientRedirectionRepository;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.api.repository.ClientRequestUriRepository;
import ca.n4dev.aegaeon.api.repository.ClientScopeRepository;
import ca.n4dev.aegaeon.server.controller.dto.PageDto;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
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
public class ClientService extends BaseSecuredService<Client, ClientRepository> {

    private ClientMapper clientMapper;
    private ClientGrantTypeRepository clientGrantTypeRepository;
    private ClientScopeRepository clientScopeRepository;
    private ClientRedirectionRepository clientRedirectionRepository; 
    private ClientRequestUriRepository clientRequestUriRepository;
    private ClientContactRepository clientContactRepository;
    
    /**
     * Default Constructor.
     * @param pRepository Client repository.
     */
    @Autowired
    public ClientService(ClientRepository pRepository, 
                         ClientGrantTypeRepository pClientGrantTypeRepository,
                         ClientScopeRepository pClientScopeRepository,
                         ClientRedirectionRepository pClientRedirectionRepository, 
                         ClientRequestUriRepository pClientRequestUriRepository,
                         ClientContactRepository pClientContactRepository,
                         ClientMapper pClientMapper) {
        super(pRepository);

        this.clientGrantTypeRepository = pClientGrantTypeRepository;
        this.clientScopeRepository = pClientScopeRepository;
        this.clientRedirectionRepository = pClientRedirectionRepository;
        this.clientRequestUriRepository = pClientRequestUriRepository;
        this.clientContactRepository = pClientContactRepository;
        this.clientMapper = pClientMapper;
        
    }

    /**
     * 
     * @param pPageable
     * @return
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ClientView> findByPage(Pageable pPageable) {
        Page<Client> page = getRepository().findAll(pPageable);
        
        List<ClientView> dtos = page.getContent().stream()
                                        .map(c -> clientMapper.clientToClientDto(c, null, null, null, null))
                                        .collect(Collectors.toList());
        
        return new PageDto<>(dtos, pPageable, page.getTotalElements());
    }

    Client findByPublicId(String pPublicId) {
        return this.getRepository().findByPublicId(pPublicId);
    }
    
    List<ClientRedirection> findRedirectionsByclientId(Long pClientId) {
        return this.clientRedirectionRepository.findByClientId(pClientId);
    }
    
    List<ClientGrantType> findGrantTypesByclientId(Long pClientId) {
        return this.clientGrantTypeRepository.findByClientId(pClientId);
    }

    List<ClientScope> findScopeByClientId(Long pClientId) {
        return this.clientScopeRepository.findByClientId(pClientId);
    }
    
    List<ClientContact> findContactByClientId(Long pClientId) {
        return this.clientContactRepository.findByClientId(pClientId);
    }
    
    @Transactional(readOnly = true)
    public boolean hasScope(Long pClientId, String pScope) {
        
        if (pClientId != null && Utils.isNotEmpty(pScope)) {
            List<ClientScope> clientScopes = this.findScopeByClientId(pClientId);
            
            return Utils.isOneTrue(clientScopes, cs -> cs.getScope().getName().equals(pScope));
        }
        
        return false;
    }
    
    @Transactional(readOnly = true)
    public boolean hasGrantType(Long pClientId, String pGrantType) {
        
        if (pClientId != null && Utils.isNotEmpty(pGrantType)) {
            List<ClientGrantType> clientGrants = this.findGrantTypesByclientId(pClientId);
            
            return Utils.isOneTrue(clientGrants, cg -> cg.getGrantType().getCode().equals(pGrantType));
        }
        
        return false;
    }

    @Transactional(readOnly = true)
    public boolean hasRedirectionUri(Long pClientId, String pRedirectionUri) {
        
        if (pClientId != null && Utils.isNotEmpty(pRedirectionUri)) {
            List<ClientRedirection> clientGrants = this.findRedirectionsByclientId(pClientId);
            
            return Utils.isOneTrue(clientGrants, r -> r.getUrl().equals(pRedirectionUri));
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#findById(java.lang.Long)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ClientView findOne(Long pId) {
        
        if (pId != null) {
            Client client = super.findById(pId);
            
            if (client != null) {
                
                List<ClientScope> clientScopes = this.findScopeByClientId(pId);
                List<ClientRedirection> clientRedirections = this.findRedirectionsByclientId(pId);
                List<ClientContact> contacts = this.findContactByClientId(pId);
                List<ClientGrantType> clientGrantTypes = this.findGrantTypesByclientId(pId);
                ClientView clientView = this.clientMapper.clientToClientDto(client, 
                                                                            clientScopes, 
                                                                            clientRedirections, 
                                                                            contacts, 
                                                                            clientGrantTypes);
                
                //clientView.setGrants(clientMapper.);
                
                return clientView;
            }
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#save(ca.n4dev.aegaeon.api.model.BaseEntity)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ClientView save(ClientView pEntity) {
        return pEntity;
//        Client client = this.clientDtoConverter.toEntity(pEntity);
//        return this.clientDtoConverter.toDto(super.save(client));
    }


    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#delete(ca.n4dev.aegaeon.api.model.BaseEntity)
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long pClientId) {
        super.delete(pClientId);
    }
    
}
