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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.n4dev.aegaeon.api.exception.InvalidScopeException;
import ca.n4dev.aegaeon.server.model.Scope;
import ca.n4dev.aegaeon.server.repository.ScopeRepository;
import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * ScopeService.java
 * 
 * Service managing scopes.
 *
 * @author by rguillemette
 * @since May 28, 2017
 */
@Service
public class ScopeService extends BaseService<Scope, ScopeRepository> {

    private static final String SPACE = " ";
    
    /**
     * Default Constructor.
     * @param pRepository The repository used by this service.
     */
    @Autowired
    public ScopeService(ScopeRepository pRepository) {
        super(pRepository);
    }

    @Transactional(readOnly = true)
    public List<Scope> findScopeFromString(String pScopeStr) throws InvalidScopeException {
        if (Utils.isEmpty(pScopeStr)) {
            return Collections.emptyList();
        }
        
        // Split on space
        List<String> scopes = parseScopeArgumentString(SPACE, pScopeStr);
        
        return findScopeFromStringList(scopes);
    }
    
    @Transactional(readOnly = true)
    public List<Scope> findScopeFromStringList(List<String> pScopeStrs) throws InvalidScopeException {
        List<Scope> lst = this.getRepository().findByNameIn(pScopeStrs);
        
        // Must match or one scope is invalid
        if (pScopeStrs.size() != lst.size()) {
            
            InvalidScopeException scex = new InvalidScopeException();
            boolean ok = false;
            for (String st : pScopeStrs) {
                ok = false;
                for (Scope sc : lst) {
                    if (sc.getName().equals(st)) {
                        ok = true;
                        break;
                    }
                }
                
                if (!ok) {
                    scex.addInvalidScope(st);
                }
            }
            
            throw scex;
        }
        
        return lst;
    }
    
    public List<String> parseScopeArgumentString(String pSeparator, String pScopeStr) {
     
        if (Utils.isEmpty(pScopeStr)) {
            return Collections.emptyList();
        }
        
        String[] scopeStrs = pScopeStr.split(pSeparator);
        List<String> scopes = new ArrayList<>();
        
        // Create a list and clean empty
        for (String s : scopeStrs) {
            if (Utils.isNotEmpty(s)) {
                scopes.add(s);
            }
        }
        
        return scopes;
    }
}
