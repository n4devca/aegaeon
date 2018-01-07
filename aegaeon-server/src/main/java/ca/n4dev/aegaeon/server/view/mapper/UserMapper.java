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

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import ca.n4dev.aegaeon.api.model.Authority;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import ca.n4dev.aegaeon.server.view.UserView;

/**
 * UserMapper.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Dec 13, 2017
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
        @Mapping(target = "picture", source = "pUser.pictureUrl"),
        @Mapping(target = "roles", source = "pUser.authorities"),
        @Mapping(target = "userInfos", source = "pUserInfos")
    })
    UserView toView(User pUser, List<UserInfo> pUserInfos);
    
    @InheritInverseConfiguration
    @Mappings({
        @Mapping(target = "pictureUrl", source = "picture"),
        @Mapping(source = "roles", target = "authorities"),
        @Mapping(target = "version", ignore = true)
    })
    User toEntity(UserView pUserView);

    @Mappings({
        @Mapping(target = "refId", source = "id"),
        @Mapping(target = "name", source = "otherName"),
        @Mapping(target = "code", source = "type.code"),
        @Mapping(target = "category", source = "type.parent.code"),
        @Mapping(target = "refTypeId", source = "type.id")
    })
    UserInfoView userInfoToView(UserInfo pUserInfo);
    
    default String authorityToString(Authority pAuthority) {
        return pAuthority.getCode();
    }
    
    default Authority stringToAuthority(String pRole) {
        Authority a = new Authority();
        a.setCode(pRole);
        return a;
    }
    
}
