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
package ca.n4dev.aegaeon.server.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.n4dev.aegaeon.api.protocol.Flow;
import ca.n4dev.aegaeon.api.protocol.FlowFactory;
import ca.n4dev.aegaeon.api.protocol.RequestedGrant;
import ca.n4dev.aegaeon.server.controller.OAuthTokensController;
import ca.n4dev.aegaeon.server.model.RefreshToken;
import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.RefreshTokenService;

/**
 * TokenControllerTest.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 8, 2017
 */
@AutoConfigureMockMvc
public class TokenControllerTest extends BaseWebTest {

    private static final String AUTH_CODE_A = "0xffA";
    private static final String AUTH_CODE_B = "0xffB";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    //@Before
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(OAuthTokensController.class)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    public void successGetAccessToken() throws Exception {
         
        String auth  = "Basic Y2EubjRkZXYuYXV0aC5jbGllbnQ6a2phc2thczg5OTNqbnNrYWprc2o=";
        
        MvcResult result =
                this.mockMvc.perform(
                        post(OAuthTokensController.URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("code", AUTH_CODE_A)
                            .param("grant_type", FlowFactory.PARAM_CODE + " " + FlowFactory.PARAM_ID_TOKEN)
                            .param("client_id", "ca.n4dev.auth.client")
                            .param("scope", "openid profile")
                            .param("redirect_uri", "http://localhost/login.html")
                            .header("Authorization", auth)
                            ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assert.assertNotNull(result);
        String response = result.getResponse().getContentAsString();
        
        Assert.assertNotNull(response);
        
    }
    
    @Test
    public void successGetAccessTokenFromRefresh() throws Exception {
        String auth  = "Basic Y2EubjRkZXYuYXV0aC5jbGllbnQ6a2phc2thczg5OTNqbnNrYWprc2o=";
        
        RefreshToken token = this.refreshTokenService.findByTokenValueAndClientId("9b65047c-93ce-4934-beb5-9e3239c2981b", 1L);
        
        MvcResult result =
                this.mockMvc.perform(
                        post(OAuthTokensController.URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("refresh_token", token.getToken())
                            .param("grant_type", FlowFactory.PARAM_REFRESH_TOKEN)
                            .param("client_id", "ca.n4dev.auth.client")
                            .param("scope", "openid profile")
                            .param("redirect_uri", "http://localhost/login.html")
                            .header("Authorization", auth)
                            ).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assert.assertNotNull(result);
        String response = result.getResponse().getContentAsString();
        
        System.out.println(response);
        
    }
    
}
