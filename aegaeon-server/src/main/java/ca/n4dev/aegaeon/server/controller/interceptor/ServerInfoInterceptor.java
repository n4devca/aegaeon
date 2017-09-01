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

package ca.n4dev.aegaeon.server.controller.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.n4dev.aegaeon.server.config.ServerInfo;

/**
 * ServerInfoInterceptor
 * 
 * Inject server info into all view.
 * 
 * @author by rguillemette
 * @since May 24, 2017
 */
public class ServerInfoInterceptor extends HandlerInterceptorAdapter {

	private ServerInfo serverInfo;
	
	/**
	 * Default Constructor.
	 * @param pServerInfo This server info.
	 */
	public ServerInfoInterceptor(ServerInfo pServerInfo) {
		this.serverInfo = pServerInfo;
	}
	
	@Override
	public void postHandle(HttpServletRequest pRequest, HttpServletResponse pResponse, Object pHandler,
								ModelAndView pModelAndView) throws Exception {

		pModelAndView.addObject("serverInfo", this.serverInfo);
	}
}
