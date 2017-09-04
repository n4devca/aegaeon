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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * SimpleLoginController.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 14, 2017
 */
@Controller
@RequestMapping(SimpleLoginController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "login", havingValue = "true", matchIfMissing = true)
public class SimpleLoginController {
	
	public static final String URL = "/login";

    @RequestMapping("")
    public ModelAndView login() {
        return new ModelAndView("signinpage");
    }
}
