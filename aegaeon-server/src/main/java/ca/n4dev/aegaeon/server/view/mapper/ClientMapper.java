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
package ca.n4dev.aegaeon.server.view.mapper;

import java.util.List;

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientAuthFlow;
import ca.n4dev.aegaeon.api.model.ClientContact;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ClientView;
import ca.n4dev.aegaeon.server.view.Selection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClientMapper.java
 * <p>
 * Client view mapper.
 *
 * @author by rguillemette
 * @since Dec 8, 2017
 */
@Component
public class ClientMapper {

    @Autowired
    private ScopeMapper scopeMapper;

    @Autowired
    private SelectionMapper selectionMapper;

    public ClientView clientToClientDto(Client pClient,
                                        List<Selection<ClientScope>> pClientScopes,
                                        List<ClientRedirection> pClientRedirections,
                                        List<ClientContact> pClientContacts,
                                        List<Selection<ClientAuthFlow>> pClientGrantTypes) {

        final ClientView clientView = new ClientView();

        if (pClient != null) {
            clientView.setId(pClient.getId());
            clientView.setPublicId(pClient.getPublicId());
            clientView.setDescription(pClient.getDescription());
            clientView.setName(pClient.getName());
            clientView.setProviderType(pClient.getProviderName());
            clientView.setIdTokenSeconds(pClient.getIdTokenSeconds());
            clientView.setAccessTokenSeconds(pClient.getAccessTokenSeconds());
            clientView.setRefreshTokenSeconds(pClient.getRefreshTokenSeconds());
            clientView.setSecret(pClient.getSecret());
            clientView.setLogoUrl(pClient.getLogoUrl());
        }

        clientView.setScopes(Utils.convert(pClientScopes, selectionMapper::clientScopeToSelectableItemView));
        clientView.setRedirections(Utils.convert(pClientRedirections, this::redirectionToString));
        clientView.setContacts(Utils.convert(pClientContacts, this::contactToString));
        clientView.setGrants(Utils.convert(pClientGrantTypes, selectionMapper::clientAuthFlowToSelectableItemView));

        return clientView;
    }

    public Client clientViewToClient(ClientView pClientView, Client pClient) {

        if (pClientView != null) {

            pClient.setPublicId(pClientView.getPublicId());
            pClient.setDescription(pClientView.getDescription());
            pClient.setName(pClientView.getName());
            pClient.setProviderName(pClientView.getProviderType());
            pClient.setIdTokenSeconds(pClientView.getIdTokenSeconds());
            pClient.setAccessTokenSeconds(pClientView.getAccessTokenSeconds());
            pClient.setRefreshTokenSeconds(pClientView.getRefreshTokenSeconds());
            pClient.setLogoUrl(pClientView.getLogoUrl());
            // pClient.setSecret(pClient.getSecret());

        }

        return pClient;
    }

    String redirectionToString(ClientRedirection pClientRedirection) {
        return pClientRedirection.getUrl();
    }

    String contactToString(ClientContact pClientContact) {
        return pClientContact.getEmail();
    }
}
