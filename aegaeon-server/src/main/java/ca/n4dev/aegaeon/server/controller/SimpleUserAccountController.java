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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ca.n4dev.aegaeon.server.model.User;
import ca.n4dev.aegaeon.server.security.SpringAuthUserDetails;
import ca.n4dev.aegaeon.server.service.UserService;

/**
 * SimpleUserProfileController.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 12, 2017
 */
@Controller
@RequestMapping(value = "/user-account")
public class SimpleUserAccountController {

    private UserService userService;
    
    /**
     * Default Constructor.
     * @param pUserService Service to access user's informations
     */
    @Autowired
    public SimpleUserAccountController(UserService pUserService) {
        this.userService = pUserService;
    }
    
    @RequestMapping("")
    public ModelAndView account(@AuthenticationPrincipal SpringAuthUserDetails pUser) {
        ModelAndView mv = new ModelAndView("user-account");
        
        // user
        User u = this.userService.findById(pUser.getId());
        
        mv.addObject("user", u);
        
        return mv;
    }
}
