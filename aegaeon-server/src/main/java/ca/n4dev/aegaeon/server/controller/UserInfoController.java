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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.n4dev.aegaeon.server.security.AccessTokenAuthentication;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.view.UserInfoResponseView;

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
    
    /**
     * Default Constructor.
     * @param pUserService the user service to get the user's info.
     */
    @Autowired
    public UserInfoController(UserService pUserService) {
        this.userService = pUserService;
    }
    
    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public UserInfoResponseView userInfo(Authentication pAuthentication) {
        
        if (pAuthentication != null && pAuthentication instanceof AccessTokenAuthentication) {
            return this.userService.info((AccessTokenAuthentication) pAuthentication);
        }

        //TODO(RG) Throw exception instead
        return null;
    }
}
