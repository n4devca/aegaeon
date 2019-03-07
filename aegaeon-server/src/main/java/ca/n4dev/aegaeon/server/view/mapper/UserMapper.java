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

import ca.n4dev.aegaeon.api.model.Authority;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.model.UserInfo;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.UserInfoView;
import ca.n4dev.aegaeon.server.view.UserView;
import org.springframework.stereotype.Component;

/**
 * UserMapper.java
 *
 * Mapper between user and view.
 *
 * @author by rguillemette
 * @since Dec 13, 2017
 */
@Component
public class UserMapper {

    public UserView toView(User pUser, List<UserInfo> pUserInfos) {
        UserView uv = new UserView();

        uv.setId(pUser.getId());
        uv.setUniqueIdentifier(pUser.getUniqueIdentifier());
        uv.setName(pUser.getName());
        uv.setUserName(pUser.getUserName());

        uv.setPicture(pUser.getPictureUrl());
        uv.setRoles(Utils.convert(pUser.getAuthorities(), this::authorityToString));
        uv.setUserInfos(Utils.convert(pUserInfos, this::toUserInfoToView));

        return uv;
    }

    public User toEntity(UserView pUserView) {
        User user = new User();

        user.setName(pUserView.getName());
        user.setPictureUrl(pUserView.getPicture());
        user.setAuthorities(Utils.convert(pUserView.getRoles(), this::stringToAuthority));

        return user;
    }

    public UserInfoView toUserInfoToView(UserInfo pUserInfo) {
        UserInfoView uiv = new UserInfoView();

        uiv.setRefId(pUserInfo.getId());
        uiv.setName(pUserInfo.getDescription());
        uiv.setValue(pUserInfo.getValue());

        if (pUserInfo.getType() != null) {
            uiv.setCode(pUserInfo.getType().getCode());
            uiv.setRefTypeId(pUserInfo.getType().getId());
        }

        return uiv;
    }

    public String authorityToString(Authority pAuthority) {
        return pAuthority.getCode();
    }

    public Authority stringToAuthority(String pRole) {
        Authority a = new Authority();
        a.setCode(pRole);
        return a;
    }

}
