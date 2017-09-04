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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * SimpleHomeController.java
 * 
 * A simple controller managing the homepage or redirecting to user-account if the home is disabled.
 * 
 * @author by rguillemette
 * @since Jul 14, 2017
 */
@Controller
@RequestMapping(value = SimpleHomeController.URL)
//@ConditionalOnProperty(prefix = "aegaeon.modules", name = "home", havingValue = "true", matchIfMissing = false)
public class SimpleHomeController {

	public static final String URL = "/";
	
	private String homeModule;
	
	/**
	 * Default Constructor.
	 * @param pHomeModuleEnable If home is enabled.
	 */
	public SimpleHomeController(@Value("aegaeon.modules.home:false") String pHomeModuleEnable) {
		this.homeModule = pHomeModuleEnable;
	}
	
    /**
     * @return Aegaeon home page.
     */
    @RequestMapping("")
    public ModelAndView home() {
  
    	if (Utils.FALSE.equalsIgnoreCase(homeModule)) {
    		// home is disabled
    		return new ModelAndView(new RedirectView(SimpleUserAccountController.URL, true));
    	}
    	
    	return new ModelAndView("homepage");
    }
}
