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
package ca.n4dev.aegaeon.server.view.mapper;

import java.util.List;

import ca.n4dev.aegaeon.server.view.Selection;
import org.mapstruct.*;

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.model.ClientContact;
import ca.n4dev.aegaeon.api.model.ClientAuthFlow;
import ca.n4dev.aegaeon.api.model.ClientRedirection;
import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.server.view.ClientView;

/**
 * ClientMapper.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 8, 2017
 */
@Mapper(componentModel = "spring", uses = {ScopeMapper.class, SelectionMapper.class})
public interface ClientMapper {
    
    @Mappings({
        @Mapping(target = "providerType", source = "pClient.providerName"),
        @Mapping(target = "scopes", source = "pClientScopes"),
        @Mapping(target = "redirections", source = "pClientRedirections"),
        @Mapping(target = "contacts", source = "pClientContacts"),
        @Mapping(target = "grants", source = "pClientGrantTypes")
    })
    ClientView clientToClientDto(Client pClient, 
                                 List<Selection<ClientScope>> pClientScopes,
                                 List<ClientRedirection> pClientRedirections, 
                                 List<ClientContact> pClientContacts,
                                 List<Selection<ClientAuthFlow>> pClientGrantTypes);
    
    @InheritInverseConfiguration
    @Mappings({
        @Mapping(target = "providerName", source = "providerType"),
        @Mapping(target = "version", ignore = true),
        @Mapping(target = "id", ignore = true)
    })
    void clientViewToclient(ClientView pClientView, @MappingTarget Client pClient);
    
    default String redirectionToString(ClientRedirection pClientRedirection) {
        return pClientRedirection.getUrl();
    }
    
    default String contactToString(ClientContact pClientContact) {
        return pClientContact.getEmail();
    }
}
