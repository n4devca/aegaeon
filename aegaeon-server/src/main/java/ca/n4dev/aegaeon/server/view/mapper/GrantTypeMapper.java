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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import ca.n4dev.aegaeon.api.model.ClientGrantType;
import ca.n4dev.aegaeon.api.model.GrantType;
import ca.n4dev.aegaeon.server.view.SelectableItemView;

/**
 * GrantTypeMapper.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 11, 2017
 */
@Mapper
public interface GrantTypeMapper {

    @Mappings({
        @Mapping(target = "name", source = "code"),
        @Mapping(target = "description", ignore = true),
        @Mapping(target = "selected", ignore = true),
    })
    SelectableItemView grantTypeToSelectableItemView(GrantType pGrantType);
    
    @Mappings({
        @Mapping(target = "id", source = "grantType.id"),
        @Mapping(target = "name", source = "grantType.code"),
        @Mapping(target = "description", ignore = true),
    })
    SelectableItemView clientGrantTypeToSelectableItemView(ClientGrantType pClientGrantType);
    
}
