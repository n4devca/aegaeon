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
package ca.n4dev.aegaeon.server.controller;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.token.payload.PayloadProvider;
import ca.n4dev.aegaeon.server.controller.dto.UserInfoResponse;
import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthentication;
import ca.n4dev.aegaeon.server.service.UserService;

/**
 * UserInfoController.java
 * 
 * Return user's information (restricted by scopes) following 
 * a successful authentication by access token.
 *
 * @author by rguillemette
 * @since Jul 16, 2017
 */
@Controller
@RequestMapping(value = UserInfoController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "oauth", havingValue = "true", matchIfMissing = true)
public class UserInfoController {

    public static final String URL = "/userinfo";
    
    private UserService userService;
    private PayloadProvider payloadProvider;
    private OpenIdEventLogger openIdEventLogger;
    
    /**
     * Default Constructor.
     * @param pUserService The user's service.
     * @param pPayloadProvider A payload provider.
     * @param pOpenIdEventLogger The logger service.
     */
    public UserInfoController(UserService pUserService, PayloadProvider pPayloadProvider, OpenIdEventLogger pOpenIdEventLogger) {
        this.userService = pUserService;
        this.payloadProvider = pPayloadProvider;
        this.openIdEventLogger = pOpenIdEventLogger;
    }
    
    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public UserInfoResponse userInfo(Authentication pAuthentication) {
        
        if (pAuthentication != null && pAuthentication instanceof AccessTokenAuthentication) {
            
            AccessTokenAuthentication auth = (AccessTokenAuthentication) pAuthentication;
            User u = this.userService.findById(auth.getUserId());
            Map<String, String> payload = this.payloadProvider.createPayload(u, null, auth.getScopes());

            UserInfoResponse response = new UserInfoResponse(auth.getUniqueIdentifier(), payload);
            
            openIdEventLogger.log(OpenIdEvent.REQUEST_INFO, getClass(), u.getUserName(), null);
            
            return response;
        }
        
        return null;
    }
}
