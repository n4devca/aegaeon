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


import ca.n4dev.aegaeon.server.controller.validator.CreateAccountViewValidator;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.view.CreateAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

/**
 * SimpleCreateAccountController.java
 *
 * Controller driving user account creation.
 *
 * Can be enable/disable using aegaeon.modules.createaccount flag.
 *
 * @author by rguillemette
 * @since Jul 14, 2017
 */
@Controller
@RequestMapping(SimpleCreateAccountController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "createaccount", havingValue = "true", matchIfMissing = true)
public class SimpleCreateAccountController {

    private static final String SUB_URL_ACCEPT = "/accept";

    public static final String URL = "/create-account";
    public static final String URL_ACCEPT = URL + SUB_URL_ACCEPT;
    public static final String VIEW = "/user/create-account";

    private CreateAccountViewValidator validator;
    private UserService userService;

    /**
     *
     * @param pCreateAccountViewValidator
     */
    @Autowired
    public SimpleCreateAccountController(UserService pUserService, CreateAccountViewValidator pCreateAccountViewValidator) {
        this.userService = pUserService;
        this.validator = pCreateAccountViewValidator;
    }

    @PostMapping("")
    public ModelAndView getCreateAccountPage(Locale pLocale) {
        ModelAndView mv = new ModelAndView(VIEW);
        mv.addObject("user", new CreateAccountView());

        return mv;
    }


    @PostMapping(SUB_URL_ACCEPT)
    public ModelAndView createAccountPage(@ModelAttribute("user") CreateAccountView pCreateAccountView, BindingResult pResult, Locale pLocale) {
        ModelAndView mv;
        this.validator.validate(pCreateAccountView, pResult);

        if (pResult.hasErrors()) {
            mv = new ModelAndView(VIEW, pResult.getModel());
            mv.addObject("user", pCreateAccountView);
        } else {
            mv = new ModelAndView(VIEW);

            this.userService.create(pCreateAccountView.getUsername(), "", pCreateAccountView.getPassword());

            mv.addObject("completed", true);

        }


        return mv;
    }

}
