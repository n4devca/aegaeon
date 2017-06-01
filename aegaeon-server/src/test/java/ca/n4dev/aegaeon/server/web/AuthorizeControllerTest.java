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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.n4dev.aegaeon.server.controller.OAuthAuthorizationController;


/**
 * LoginTest.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 30, 2017
 */
//@WebMvcTest(controllers = {OAuthAuthorizationController.class})
@AutoConfigureMockMvc()
public class AuthorizeControllerTest extends BaseWebTest {

//    @Autowired
//    private MockMvc mvc;
    
    private MockMvc mockMvc;
    
    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(OAuthAuthorizationController.class)
                .build();
    }
    
    
    @Test
    public void testMissingParams() throws Exception {
        
        MvcResult result =
                this.mockMvc.perform(get("/authorize").accept(MediaType.TEXT_HTML))
                            .andReturn();
        
        Assert.assertNotNull(result);
        System.out.println(result.getResponse().getStatus());
    }
   
}
