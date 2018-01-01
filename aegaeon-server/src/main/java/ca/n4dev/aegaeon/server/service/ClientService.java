/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.service;

import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.*;
import ca.n4dev.aegaeon.api.protocol.ClientConfig;
import ca.n4dev.aegaeon.api.repository.*;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.server.controller.dto.PageDto;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Differentiation;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import ca.n4dev.aegaeon.server.view.mapper.ClientMapper;
import ca.n4dev.aegaeon.server.view.mapper.GrantTypeMapper;
import ca.n4dev.aegaeon.server.view.mapper.ScopeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * ClientService.java
 * <p>
 * Service managing client.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Service
public class ClientService extends BaseSecuredService<Client, ClientRepository> {

    private ClientMapper clientMapper;
    private ScopeMapper scopeMapper;
    private GrantTypeMapper grantTypeMapper;

    private ClientGrantTypeRepository clientGrantTypeRepository;
    private ClientScopeRepository clientScopeRepository;
    private ClientRedirectionRepository clientRedirectionRepository;
    private ClientRequestUriRepository clientRequestUriRepository;
    private ClientContactRepository clientContactRepository;

    /**
     * Default Constructor.
     *
     * @param pRepository Client repository.
     */
    @Autowired
    public ClientService(ClientRepository pRepository,
                         ClientGrantTypeRepository pClientGrantTypeRepository,
                         ClientScopeRepository pClientScopeRepository,
                         ClientRedirectionRepository pClientRedirectionRepository,
                         ClientRequestUriRepository pClientRequestUriRepository,
                         ClientContactRepository pClientContactRepository,
                         ClientMapper pClientMapper,
                         ScopeMapper pScopeMapper,
                         GrantTypeMapper pGrantTypeMapper) {

        super(pRepository);

        this.clientGrantTypeRepository = pClientGrantTypeRepository;
        this.clientScopeRepository = pClientScopeRepository;
        this.clientRedirectionRepository = pClientRedirectionRepository;
        this.clientRequestUriRepository = pClientRequestUriRepository;
        this.clientContactRepository = pClientContactRepository;

        this.clientMapper = pClientMapper;
        this.scopeMapper = pScopeMapper;
        this.grantTypeMapper = pGrantTypeMapper;
    }

    /**
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
    @Transactional(readOnly = true)
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

    /**
     * Save a client view.
     *
     * @param pClientId
     * @param pEntity   The entity to update
     * @return the client as view
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ClientView update(Long pClientId, ClientView pEntity) {

        // Mandatory info
        Assert.notNull(pClientId, ServerExceptionCode.ENTITY_ID_EMPTY);
        Assert.notNull(pEntity, ServerExceptionCode.ENTITY_EMPTY);
        if (Utils.areOneEmpty(pEntity.getName(), pEntity.getPublicId(), pEntity.getSecret(), pEntity.getProviderType())) {
            Utils.raise(ServerExceptionCode.CLIENT_ATTR_EMPTY);
        }

        // Get the client from db
        Client client = this.findById(pClientId);
        Assert.notNull(client, ServerExceptionCode.ENTITY_EMPTY);

        // Validate view
        validateUpdatedView(client, pEntity);

        // update client entity
        this.clientMapper.clientViewToclient(pEntity, client);
        // Re-set default value if empty
        enforceProperValues(client);
        // Save client
        client = this.save(client);

        // Update dependencies
        updateScope(pClientId, pEntity.getScopes());
        updateGrantTypes(pClientId, pEntity.getGrants());
        updateRedirectionUrls(pClientId, pEntity.getRedirections());
        updateContacts(pClientId, pEntity.getContacts());


        return this.findOne(pClientId);
    }

    private void updateRedirectionUrls(Long pClientId, List<String> pUrls) {
        List<ClientRedirection> redirections = this.findRedirectionsByclientId(pClientId);

        Differentiation<ClientRedirection> diff = Utils.differentiate(redirections, pUrls,
                (redUrl, url) -> redUrl.getUrl().equalsIgnoreCase(url),
                (redUrl, url) -> redUrl,
                url -> new ClientRedirection(new Client(pClientId), url));

        // Create / Update existing
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientRedirectionRepository::save);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientRedirectionRepository::delete);

    }

    private void updateContacts(Long pClientId, List<String> pContacts) {
        List<ClientContact> contacts = this.findContactByClientId(pClientId);

        Differentiation<ClientContact> diff = Utils.differentiate(contacts, pContacts,
                (contact, ctString) -> Utils.equals(contact.getEmail(), ctString),
                (contact, ctString) -> contact,
                ctString -> new ClientContact(new Client(pClientId), ctString));

        // Create / Update existing
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientContactRepository::save);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientContactRepository::delete);
    }

    private void updateGrantTypes(Long pClientId, List<SelectableItemView> pGrantTypesView) {
        List<ClientGrantType> grants = this.findGrantTypesByclientId(pClientId);
        List<ClientGrantType> grantViews = this.grantTypeMapper.selectableItemViewsToClientGrantTypes(pGrantTypesView);

        Differentiation<ClientGrantType> diff =
                Utils.differentiate(grants, grantViews,
                        (clientGrant, viewGrant) -> clientGrant.getGrantType().getId().equals(viewGrant.getGrantType().getId()),
                        (clientGrant, viewGrant) -> {
                            clientGrant.setSelected(viewGrant.isSelected());
                            return clientGrant;
                        },
                        newGrant -> new ClientGrantType(new Client(pClientId), newGrant.getGrantType(), newGrant.isSelected()));

        // Create / Update existing
        Utils.isNotEmptyThen(diff.getUpdatedObjs(), this.clientGrantTypeRepository::save);
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientGrantTypeRepository::save);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientGrantTypeRepository::delete);
    }

    private void updateScope(Long pClientId, List<SelectableItemView> pClientScopesView) {
        List<ClientScope> currentScopes = this.findScopeByClientId(pClientId);
        List<ClientScope> viewScopes = this.scopeMapper.scopeViewsToClientScopes(pClientScopesView);

        Differentiation<ClientScope> diff = Utils.differentiate(currentScopes, viewScopes,
                (c, v) -> c.getScope().getId().equals(v.getScope().getId()),
                (c, v) -> {
                    c.setSelected(v.isSelected());
                    return c;
                },
                (n) -> new ClientScope(new Client(pClientId), n.getScope(), n.isSelected()));

        // Create / Update existing
        Utils.isNotEmptyThen(diff.getUpdatedObjs(), this.clientScopeRepository::save);
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientScopeRepository::save);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientScopeRepository::delete);
    }

    private void validateUpdatedView(Client pClient, ClientView pClientView) {

        // If the publicId is changed, check if it does not exists already
        if (!pClient.getPublicId().equalsIgnoreCase(pClientView.getPublicId()) &&
                this.getRepository().existsByPublicId(pClientView.getPublicId())) {
            Utils.raise(ServerExceptionCode.CLIENT_DUPLICATE_PUBLICID);
        }


        Utils.isNotEmptyThen(pClientView.getScopes(), scopes -> {
            scopes.forEach(s -> {
                Assert.notNull(s.getId(), ServerExceptionCode.CLIENT_ATTR_INVALID, "ClientScope (view) without id.");
            });
        });

        Utils.isNotEmptyThen(pClientView.getGrants(), grants -> {
            grants.forEach(g -> {
                Assert.notNull(g.getId(), ServerExceptionCode.CLIENT_ATTR_INVALID, "ClientGrant (view) without id.");
            });
        });

        // For contacts and redirectionUrl, empty are simply removed
        if (Utils.isNotEmpty(pClientView.getContacts())) {
            pClientView.getContacts().removeIf(Utils::isEmpty);
        }
        if (Utils.isNotEmpty(pClientView.getRedirections())) {
            pClientView.getRedirections().removeIf(Utils::isEmpty);
        }

    }

    private void enforceProperValues(Client pClient) {

        // Check token expiration times
        if (pClient.getAccessTokenSeconds() == null || pClient.getAccessTokenSeconds() < 60) {
            pClient.setAccessTokenSeconds(ClientConfig.DEFAULT_ACCESS_TOKEN_SECONDS);
        }

        if (pClient.getIdTokenSeconds() == null || pClient.getIdTokenSeconds() < 60) {
            pClient.setIdTokenSeconds(ClientConfig.DEFAULT_ID_TOKEN_SECONDS);
        }

        if (pClient.getRefreshTokenSeconds() == null || pClient.getRefreshTokenSeconds() < 60 || pClient.getRefreshTokenSeconds() > ClientConfig.MAX_REFRESH_TOKEN_SECONDS) {
            pClient.setIdTokenSeconds(ClientConfig.DEFAULT_REFRESH_TOKEN_SECONDS);
        }

        // Invalid ProviderType?
        if (TokenProviderType.from(pClient.getProviderName()) == null) {
            pClient.setProviderName(ClientConfig.DEFAULT_PROVIDER_TYPE.toString());
        }


    }


    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.service.BaseService#delete(ca.n4dev.aegaeon.api.model.BaseEntity)
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long pClientId) {
        super.delete(pClientId);
    }

}
