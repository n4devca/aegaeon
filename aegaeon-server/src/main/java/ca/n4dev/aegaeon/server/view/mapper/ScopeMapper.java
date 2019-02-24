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

import ca.n4dev.aegaeon.api.model.ClientScope;
import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.SelectableItemView;
import org.springframework.stereotype.Component;

/**
 * ScopeMapper.java
 * <p>
 * Scope mapper.
 *
 * @author by rguillemette
 * @since Dec 10, 2017
 */
//@Mapper(componentModel = "spring")
@Component
public class ScopeMapper {

    public SelectableItemView scopeToScopeView(Scope pScope) {
        SelectableItemView selectableItemView = new SelectableItemView();

        if (pScope != null) {
            selectableItemView.setId(pScope.getId());
            selectableItemView.setName(pScope.getName());
            selectableItemView.setDescription(pScope.getDescription());
        }

        return selectableItemView;
    }

    public SelectableItemView scopeToScopeView(ClientScope pClientScope) {
        SelectableItemView selectableItemView = new SelectableItemView();

        if (pClientScope != null && pClientScope.getScope() != null) {
            selectableItemView.setId(pClientScope.getScope().getId());
            selectableItemView.setName(pClientScope.getScope().getName());
            selectableItemView.setDescription(pClientScope.getScope().getDescription());
        }

        return selectableItemView;
    }

    public ClientScope scopeViewToClientScope(SelectableItemView pSelectableItemView) {
        ClientScope clientScope = new ClientScope();

        if (pSelectableItemView != null) {

            clientScope.setScope(new Scope());
            clientScope.getScope().setId(pSelectableItemView.getId());
            clientScope.getScope().setName(pSelectableItemView.getName());
        }


        return clientScope;
    }


    public List<SelectableItemView> clientScopesToScopeviews(List<ClientScope> pClientScopes) {
        return Utils.convert(pClientScopes, this::scopeToScopeView);
    }

    public List<ClientScope> scopeViewsToClientScopes(List<SelectableItemView> pSelectableItemViews) {
        return Utils.convert(pSelectableItemViews, this::scopeViewToClientScope);
    }
}
