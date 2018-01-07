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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import ca.n4dev.aegaeon.server.view.ServerInfoResponse;

/**
 * SimpleServerInforController.java
 * 
 * Provide openid configuration endpoint.
 * Following http://openid.net/specs/openid-connect-discovery-1_0.html
 *
 * @author by rguillemette
 * @since Jul 26, 2017
 */
@Controller
@RequestMapping(value = ServerInfoController.URL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = "information", havingValue = "true", matchIfMissing = true)
public class ServerInfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInfoController.class);
    
    public static final String URL = "/.well-known/openid-configuration";
    
    private ServerInfo serverInfo;
    
    private TokenFactory tokenFactory;
    
    private ScopeService scopeService;
    
    @Autowired
    public ServerInfoController(ServerInfo pServerInfo, 
                                       TokenFactory pTokenFactory,
                                       ScopeService pScopeService) {
        this.serverInfo = pServerInfo;
        this.tokenFactory = pTokenFactory;
        this.scopeService = pScopeService;
    }
    
    @RequestMapping(value = "", produces = {"application/json"})
    @ResponseBody
    public ServerInfoResponse configuration(HttpServletRequest pHttpServletRequest) {
        
        String ctx = pHttpServletRequest.getContextPath();
        String issuer = this.serverInfo.getIssuer();
        
        List<Scope> allScopes = this.scopeService.findAll();
        
        ServerInfoResponse info = new ServerInfoResponse();
        
        info.setIssuer(this.serverInfo.getIssuer());
        info.setAuthorizationEndpoint(issuer + ctx + AuthorizationController.URL);
        info.setTokenEndpoint(issuer + ctx + TokensController.URL);
        info.setUserinfoEndpoint(issuer + ctx + UserInfoController.URL);
        info.setDisplayValuesSupported(Arrays.asList("page"));
        info.setClaimsParameterSupported(false);
        info.setOpTosUri(this.serverInfo.getPrivacyPolicy());
        info.setSubjectTypesSupported(Arrays.asList("public"));

        // Not completed yet but advertize anyway
        info.setClaimTypesSupported(Arrays.asList("normal", "distributed"));
        info.setUiLocalesSupported(Arrays.asList(Locale.CANADA_FRENCH.toString(), 
                                                 Locale.ENGLISH.toString()));
        
        
        // Algo
        List<String> signingAlgos = this.tokenFactory.getSupportedAlgorithm();
        
        info.setIdTokenSigningAlgValuesSupported(signingAlgos);
        info.setUserinfoSigningAlgValuesSupported(signingAlgos);
        
        // We only supported basic currently
        info.setTokenEndpointAuthMethodsSupported(Arrays.asList("client_secret_basic"));
        
        // There is an oauth in there but I think it's fine
        info.setResponseTypesSupported(Arrays.asList(FlowFactory.PARAM_TOKEN, 
                                                     FlowFactory.PARAM_CODE + " " + FlowFactory.PARAM_ID_TOKEN,
                                                     FlowFactory.PARAM_ID_TOKEN,
                                                     FlowFactory.PARAM_CLIENTCREDENTIALS));
        
        info.setScopesSupported(allScopes.stream().map(s -> s.getName()).collect(Collectors.toList()));
        
        return info;
    }
}
