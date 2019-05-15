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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientAuthFlow;
import ca.n4dev.aegaeon.api.model.ClientContact;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.protocol.ClientConfig;
import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.repository.ClientAuthFlowRepository;
import ca.n4dev.aegaeon.api.repository.ClientContactRepository;
import ca.n4dev.aegaeon.api.repository.ClientRedirectionRepository;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.api.repository.ClientScopeRepository;
import ca.n4dev.aegaeon.api.repository.ScopeRepository;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.server.controller.dto.PageDto;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Differentiation;
import ca.n4dev.aegaeon.server.utils.StringRandomizer;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import ca.n4dev.aegaeon.server.view.Selection;
import ca.n4dev.aegaeon.server.view.mapper.ClientMapper;
import ca.n4dev.aegaeon.server.view.mapper.SelectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ClientService.java
 * <p>
 * Service managing client.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Service
@CacheConfig(cacheNames = ClientService.CACHE_NAME)
public class ClientService extends BaseSecuredService<Client, ClientRepository> {

    public static final String CACHE_NAME = "ClientService";

    private ClientMapper clientMapper;
    private SelectionMapper selectionMapper;

    private ClientAuthFlowRepository clientAuthFlowRepository;
    private ClientScopeRepository clientScopeRepository;
    private ClientRedirectionRepository clientRedirectionRepository;
    private ClientContactRepository clientContactRepository;

    private ScopeRepository scopeRepository;

    /**
     * Default Constructor.
     *
     * @param pRepository Client repository.
     */
    @Autowired
    public ClientService(ClientRepository pRepository,
                         ClientAuthFlowRepository pClientGrantTypeRepository,
                         ClientScopeRepository pClientScopeRepository,
                         ClientRedirectionRepository pClientRedirectionRepository,
                         ClientContactRepository pClientContactRepository,
                         ScopeRepository pScopeRepository,
                         ClientMapper pClientMapper,
                         SelectionMapper pSelectionMapper) {

        super(pRepository);

        this.clientAuthFlowRepository = pClientGrantTypeRepository;
        this.clientScopeRepository = pClientScopeRepository;
        this.clientRedirectionRepository = pClientRedirectionRepository;
        this.clientContactRepository = pClientContactRepository;

        this.scopeRepository = pScopeRepository;

        this.clientMapper = pClientMapper;
        this.selectionMapper = pSelectionMapper;
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

    @Transactional(readOnly = true)
    public boolean hasScope(Long pClientId, String pScope) {

        if (pClientId != null && Utils.isNotEmpty(pScope)) {
            List<ClientScope> clientScopes = this.findScopeByClientId(pClientId);

            return Utils.isOneTrue(clientScopes, cs -> cs.getScope().getName().equals(pScope));
        }

        return false;
    }

    @Transactional(readOnly = true)
    public boolean hasFlow(Long pClientId, Flow pFlow) {

        if (pClientId != null && pFlow != null) {
            List<ClientAuthFlow> clientGrants = this.findAuthFlowByClientId(pClientId);
            return Utils.isOneTrue(clientGrants, cg -> cg.getFlow().equals(pFlow));
        }

        return false;
    }

    @Transactional(readOnly = true)
    public boolean hasRedirectionUri(Long pClientId, String pRedirectionUri) {

        if (pClientId != null && Utils.isNotEmpty(pRedirectionUri)) {
            List<ClientRedirection> clientGrants = this.findRedirectionsByClientId(pClientId);

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
                List<ClientRedirection> clientRedirections = this.findRedirectionsByClientId(pId);
                List<ClientContact> contacts = this.findContactByClientId(pId);
                List<ClientAuthFlow> clientGrantTypes = this.findAuthFlowByClientId(pId);


                ClientView clientView = this.clientMapper.clientToClientDto(client,
                                                                            combineScopes(client, clientScopes),
                                                                            clientRedirections,
                                                                            contacts,
                                                                            combineGrants(client, clientGrantTypes));


                return clientView;
            }
        }

        return null;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ClientView instantiateOne() {
        Client newClient = new Client(-1L);
        newClient.setSecret(StringRandomizer.getInstance().getRandomString(128));

        enforceProperValues(newClient);

        ClientView clientView = this.clientMapper.clientToClientDto(newClient,
                                                                    combineScopes(newClient, new ArrayList<>()),
                                                                    Collections.emptyList(),
                                                                    Collections.emptyList(),
                                                                    combineGrants(newClient, new ArrayList<>()));

        // Pre-select some values
        for (SelectableItemView cs : clientView.getScopes()) {
            if (cs.getName().equals("openid")) {
                cs.setSelected(true);
            }
        }


        return clientView;
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
    @CacheEvict(allEntries = true)
    public ClientView update(Long pClientId, ClientView pEntity) {

        // Mandatory info
        Assert.notNull(pClientId, ServerExceptionCode.ENTITY_ID_EMPTY);
        Assert.notNull(pEntity, ServerExceptionCode.ENTITY_EMPTY);
        if (Utils.areOneEmpty(pEntity.getName(), pEntity.getPublicId(), pEntity.getProviderType())) {
            Utils.raise(ServerExceptionCode.CLIENT_ATTR_EMPTY);
        }

        // Get the client from db or create
        boolean isNew = pClientId <= 0;
        Client client = !isNew ? this.findById(pClientId) : new Client();
        Assert.notNull(client, ServerExceptionCode.ENTITY_EMPTY);

        // Validate view
        validateUpdatedView(client, pEntity, isNew);

        // update client entity
        this.clientMapper.clientViewToClient(pEntity, client);
        // Re-set default value if empty
        enforceProperValues(client);
        // Save client
        client = this.save(client);

        Long clientId = client.getId();

        // Update dependencies
        updateScope(clientId, pEntity.getScopes());
        updateAuthFlow(clientId, pEntity.getGrants());
        updateRedirectionUrls(clientId, pEntity.getRedirections());
        updateContacts(clientId, pEntity.getContacts());


        return this.findOne(clientId);
    }

    Client findByPublicId(String pPublicId) {
        if (Utils.isNotEmpty(pPublicId)) {
            return this.getRepository().findByPublicId(pPublicId);
        }

        return null;
    }

    List<ClientRedirection> findRedirectionsByClientId(Long pClientId) {
        return this.clientRedirectionRepository.findByClientId(pClientId);
    }

    List<ClientAuthFlow> findAuthFlowByClientId(Long pClientId) {
        return this.clientAuthFlowRepository.findByClientId(pClientId);
    }

    List<ClientScope> findScopeByClientId(Long pClientId) {
        return this.clientScopeRepository.findByClientId(pClientId);
    }

    List<ClientContact> findContactByClientId(Long pClientId) {
        return this.clientContactRepository.findByClientId(pClientId);
    }


    private void updateRedirectionUrls(Long pClientId, List<String> pUrls) {
        List<ClientRedirection> redirections = this.findRedirectionsByClientId(pClientId);

        Differentiation<ClientRedirection> diff = Utils.differentiate(redirections, pUrls,
                                                                      (redUrl, url) -> redUrl.getUrl().equalsIgnoreCase(url),
                                                                      (redUrl, url) -> redUrl,
                                                                      url -> new ClientRedirection(new Client(pClientId), url));

        // Create / Update existing
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientRedirectionRepository::saveAll);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientRedirectionRepository::deleteAll);

    }

    private void updateContacts(Long pClientId, List<String> pContacts) {
        List<ClientContact> contacts = this.findContactByClientId(pClientId);

        Differentiation<ClientContact> diff = Utils.differentiate(contacts, pContacts,
                                                                  (contact, ctString) -> Utils.equals(contact.getEmail(), ctString),
                                                                  (contact, ctString) -> contact,
                                                                  ctString -> new ClientContact(new Client(pClientId), ctString));

        // Create / Update existing
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientContactRepository::saveAll);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientContactRepository::deleteAll);
    }

    private void updateAuthFlow(Long pClientId, List<SelectableItemView> pGrantTypesView) {
        List<ClientAuthFlow> clientAuthFlows = this.findAuthFlowByClientId(pClientId);
        //List<ClientAuthFlow> selectedAuthFlows = this.grantTypeMapper.selectableItemViewsToClientGrantTypes(pGrantTypesView);
        List<Selection<ClientAuthFlow>> selectedAuthFlows = this.selectionMapper.selectableItemViewsToClientAuthFlows(pGrantTypesView);

        // Remove unselected
        selectedAuthFlows = selectedAuthFlows.stream().filter(a -> a.isSelected()).collect(Collectors.toList());

        Differentiation<ClientAuthFlow> diff =
                Utils.differentiate(clientAuthFlows,
                                    selectedAuthFlows,
                                    (pClientAuthFlow, pViewFlow) -> pClientAuthFlow.getFlow() == pViewFlow.getEntity().getFlow(),
                                    (pClientAuthFlow, pViewFlow) -> pClientAuthFlow,
                                    pNewFlow -> new ClientAuthFlow(new Client(pClientId), pNewFlow.getEntity().getFlow()));

        // Create / Update existing
        // Utils.isNotEmptyThen(diff.getUpdatedObjs(), this.clientAuthFlowRepository::saveAll);
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientAuthFlowRepository::saveAll);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientAuthFlowRepository::deleteAll);
    }

    private void updateScope(Long pClientId, List<SelectableItemView> pClientScopesView) {
        List<ClientScope> currentScopes = this.findScopeByClientId(pClientId);
        //List<ClientScope> viewScopes = this.scopeMapper.scopeViewsToClientScopes(pClientScopesView);
        List<Selection<ClientScope>> viewScopes = this.selectionMapper.selectableItemViewsToClientScopes(pClientScopesView);

        viewScopes = viewScopes.stream().filter(s -> s.isSelected()).collect(Collectors.toList());

        Differentiation<ClientScope> diff = Utils.differentiate(currentScopes, viewScopes,
                                                                (c, v) -> c.getScope().getId().equals(v.getEntity().getId()),
                                                                (c, v) -> c,
                                                                (n) -> new ClientScope(new Client(pClientId), n.getEntity().getScope()));

        // Create / Update existing
        // Utils.isNotEmptyThen(diff.getUpdatedObjs(), this.clientScopeRepository::saveAll);
        Utils.isNotEmptyThen(diff.getNewObjs(), this.clientScopeRepository::saveAll);

        // Delete
        Utils.isNotEmptyThen(diff.getRemovedObjs(), this.clientScopeRepository::deleteAll);
    }

    private void validateUpdatedView(Client pClient, ClientView pClientView, boolean pIsNew) {

        // If the publicId is changed, check if it does not exists already
        if ((pIsNew || !pClient.getPublicId().equalsIgnoreCase(pClientView.getPublicId())) &&
                this.getRepository().existsByPublicId(pClientView.getPublicId())) {
            Utils.raise(ServerExceptionCode.CLIENT_DUPLICATE_PUBLICID);
        }

        // Need id for comparaison
        Utils.isNotEmptyThen(pClientView.getScopes(), scopes -> {
            scopes.forEach(s -> {
                Assert.notNull(s.getId(), ServerExceptionCode.CLIENT_ATTR_INVALID, "ClientScope (view) without id.");
            });
        });

        Utils.isNotEmptyThen(pClientView.getGrants(), grants -> {
            grants.forEach(g -> {
                Assert.notNull(g.getName(), ServerExceptionCode.CLIENT_ATTR_INVALID, "ClientGrant (view) without name.");
            });
        });


        // For contacts and redirectionUrl, empty are simply removed
        if (Utils.isNotEmpty(pClientView.getContacts())) {
            pClientView.getContacts().removeIf(Utils::isEmpty);
        }

        if (Utils.isNotEmpty(pClientView.getRedirections())) {
            // Validate Url
            pClientView.getRedirections().forEach(u -> {
                if (!Utils.validateRedirectionUri(u)) {
                    Utils.raise(ServerExceptionCode.CLIENT_REDIRECTIONURL_INVALID, u + " is invalid.");
                }
            });

            // Remove empty
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

        if (pClient.getRefreshTokenSeconds() == null || pClient.getRefreshTokenSeconds() < 60 || pClient
                .getRefreshTokenSeconds() > ClientConfig.MAX_REFRESH_TOKEN_SECONDS) {
            pClient.setRefreshTokenSeconds(ClientConfig.DEFAULT_REFRESH_TOKEN_SECONDS);
        }

        // Invalid ProviderType?
        if (TokenProviderType.from(pClient.getProviderName()) == null) {
            pClient.setProviderName(ClientConfig.DEFAULT_PROVIDER_TYPE.toString());
        }

        if (Utils.isEmpty(pClient.getSecret())) {
            pClient.setSecret(StringRandomizer.getInstance().getRandomString(128));
        }
    }

    private List<Selection<ClientScope>> combineScopes(Client pClient, List<ClientScope> pClientScopes) {

        return Utils.combine(pClientScopes,
                             this.scopeRepository.findAll(),
                             (pClientScope, pScope) -> Utils.equals(pClientScope.getScope(), pScope),
                             pScope -> new Selection<>(new ClientScope(pClient, pScope), pScope.isDefaultValue()),
                             pClientScope -> new Selection<>(pClientScope, true));

    }

    private List<Selection<ClientAuthFlow>> combineGrants(Client pClient, List<ClientAuthFlow> pClientAuthFlows) {

        return Utils.combine(pClientAuthFlows,
                             Arrays.asList(Flow.values()),
                             (pClientAuthFlow, pGrantType) -> pGrantType.equals(pClientAuthFlow.getFlow()),
                             pFlow -> new Selection<>(new ClientAuthFlow(pClient, pFlow), false),
                             pClientAuthFlow -> new Selection<>(pClientAuthFlow, true));

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
