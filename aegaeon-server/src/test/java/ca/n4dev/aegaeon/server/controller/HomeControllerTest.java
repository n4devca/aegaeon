/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HomeControllerTest.java
 *
 * A test to make sure we always have an homepage.
 *
 * @author by rguillemette
 * @since May 30, 2017
 */
@AutoConfigureMockMvc
public class HomeControllerTest extends BaseIntegratedControllerTest<SimpleHomeController> {


    @Test // ok
    public void testHome() throws Exception {

        MvcResult result =
                mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                       .andExpect(status().isOk())
                       .andReturn();

        Assert.assertNotNull(result);
    }

    @Override
    protected Class<SimpleHomeController> getControllerClass() {
        return SimpleHomeController.class;
    }
}
