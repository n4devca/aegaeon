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
package ca.n4dev.aegaeon.server.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * ScopeServiceTest.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 7, 2017
 */
public class ScopeServiceTest extends BaseServiceTest {

    @Autowired
    ScopeService scopeService;
    
    @Test
    public void testStringToScope() {
        List<Scope> all = this.scopeService.findAll();
        Assert.assertNotNull(all);
        
        String scopesStr = Utils.join(all, s -> s.getName());
        
        for (Scope s : all) {
            Assert.assertTrue(scopesStr.contains(s.getName()));
        }
        
        List<Scope> convertedAll = this.scopeService.findScopeFromString(scopesStr);
        Assert.assertNotNull(convertedAll);
        Assert.assertTrue(convertedAll.size() == all.size());
    }
    
    @Test
    public void testStringToScopeWithExcluded() {
        List<Scope> all = this.scopeService.findAll();
        Assert.assertNotNull(all);
        
        String scopesStr = Utils.join(all, s -> s.getName());
        
        for (Scope s : all) {
            Assert.assertTrue(scopesStr.contains(s.getName()));
        }
        
        List<Scope> convertedAll = this.scopeService.findScopeFromString(scopesStr, BaseTokenService.OFFLINE_SCOPE);
        Assert.assertNotNull(convertedAll);
        Assert.assertTrue(convertedAll.size() == (all.size() - 1));
    }
}
