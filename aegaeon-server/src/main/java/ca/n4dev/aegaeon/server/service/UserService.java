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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthentication;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.view.UserInfoResponseView;
import ca.n4dev.aegaeon.server.view.UserView;
import ca.n4dev.aegaeon.server.view.mapper.UserMapper;

/**
 * UserService.java
 * 
 * User service.
 *
 * @author by rguillemette
 * @since May 8, 2017
 */
@Service
public class UserService extends BaseSecuredService<User, UserRepository> {

    private OpenIdEventLogger openIdEventLogger;
    private PayloadProvider payloadProvider;
    private UserMapper userMapper;
    
    /**
     * Default constructor.
     * @param pRepository The user repo.
     */
    @Autowired
    public UserService(UserRepository pRepository, 
                       OpenIdEventLogger pOpenIdEventLogger, 
                       PayloadProvider pPayloadProvider,
                       UserMapper pUserMapper) {
        super(pRepository);
        this.openIdEventLogger = pOpenIdEventLogger;
        this.payloadProvider = pPayloadProvider;
        this.userMapper = pUserMapper;
    }

//    /**
//     * Find one user by id.
//     */
//    @Transactional(readOnly = true)
//    @PreAuthorize("hasRole('CLIENT') or principal.id == #pId")
//    User findById(Long pId) {
//        return this.getRepository().findOne(pId);
//    }
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('CLIENT') or principal.id == #pUserId")
    public UserView findOne(Long pUserId) {
        User user = super.findById(pUserId);
        
        return this.userMapper.toView(user, null);
    }
    
    @Transactional(readOnly = true)
    public UserInfoResponseView info(AccessTokenAuthentication pAccessTokenAuthentication) {
        Assert.notNull(pAccessTokenAuthentication, ServerExceptionCode.USER_EMPTY);
        
        User u = this.findById(pAccessTokenAuthentication.getUserId());
        Assert.notNull(u, ServerExceptionCode.USER_EMPTY);
        
        Map<String, String> payload = this.payloadProvider.createPayload(u, null, pAccessTokenAuthentication.getScopes());

        UserInfoResponseView response = new UserInfoResponseView(pAccessTokenAuthentication.getUniqueIdentifier(), payload);
        
        openIdEventLogger.log(OpenIdEvent.REQUEST_INFO, getClass(), u.getUserName(), null);
        
        return response;
    }
}
