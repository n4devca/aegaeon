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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ca.n4dev.aegaeon.api.model.Scope;
import ca.n4dev.aegaeon.api.repository.ScopeRepository;
import ca.n4dev.aegaeon.server.controller.exception.InvalidScopeException;
import ca.n4dev.aegaeon.server.utils.Assert;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.ScopeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<ScopeView> findAll() {
        final List<Scope> all = this.getRepository().findAll();
        return Utils.convert(all, pScope -> new ScopeView(pScope.getId(), pScope.getName()));
    }

    @Transactional(readOnly = true)
    public ScopeView findByName(String pName) {
        Assert.notEmpty(pName, () -> new InvalidScopeException(pName, null));

        final Optional<Scope> scope = this.getRepository().findByName(pName.trim());

        if (scope.isPresent()) {
            final Scope scopeObj = scope.get();
            return new ScopeView(scopeObj.getId(), scopeObj.getName());
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public ScopeSet validate(String pScopeParam, Set<String> pExclusions) {

        if (Utils.isNotEmpty(pScopeParam)) {

            Set<String> exclusions = Utils.safeSet(pExclusions);

            String[] scopeArray = pScopeParam.split(SPACE);
            Set<ScopeView> scopeViews = new HashSet<>();
            Set<ScopeView> invalidScopeViews = new HashSet<>();
            for (String s : scopeArray) {

                if (Utils.isNotEmpty(s)) {
                    String scopeValue = s.trim();

                    final Optional<Scope> optionalScope = this.getRepository().findByName(scopeValue);

                    if (optionalScope.isPresent() && !exclusions.contains(scopeValue)) {
                        final Scope scope = optionalScope.get();
                        scopeViews.add(new ScopeView(scope.getId(), scope.getName()));
                    } else {
                        invalidScopeViews.add(new ScopeView(null, s));
                    }
                }
            }

            return new ScopeSet(scopeViews, invalidScopeViews);
        }

        return ScopeSet.empty();
    }

    @Transactional(readOnly = true)
    public ScopeSet validate(String pScopeParam) {
        return validate(pScopeParam, null);
    }


    @Transactional(readOnly = true)
    public Set<ScopeView> getValidScopes(String pScopeParam) {
        return getValidScopes(pScopeParam, null);
    }

    @Transactional(readOnly = true)
    public Set<ScopeView> getValidScopes(String pScopeParam, Set<String> pExclusions) {

        if (Utils.isNotEmpty(pScopeParam)) {
            final ScopeSet scopeSet = validate(pScopeParam, pExclusions);
            return scopeSet.getValidScopes();
        }

        return Collections.emptySet();
    }

    @Transactional(readOnly = true)
    public boolean isPartOf(String pAuthorizedScopes, String pRequestedScopes) {

        final ScopeSet authorizedScopeSet = validate(pAuthorizedScopes);
        final ScopeSet requestedScopeSet = validate(pRequestedScopes);

        // Simple equals (same)
        if (authorizedScopeSet.getValidScopes().size() == requestedScopeSet.getValidScopes().size() &&
                authorizedScopeSet.getValidScopes().equals(requestedScopeSet.getValidScopes())) {
            return true;
        }

        // Check if the requested scope is a subset of the authorized
        boolean allOk = true;
        for (ScopeView scopeView : requestedScopeSet.getValidScopes()) {
            if (!authorizedScopeSet.getValidScopes().contains(scopeView)) {
                allOk = false;
                break;
            }
        }

        return allOk;
    }

    public static class ScopeSet {
        private Set<ScopeView> validScopes;
        private Set<ScopeView> invalidScopes;

        private static ScopeSet empty = new ScopeSet(Collections.emptySet(), Collections.emptySet());

        public ScopeSet(Set<ScopeView> pValidScopes, Set<ScopeView> pInvalidScopes) {
            validScopes = pValidScopes;
            invalidScopes = pInvalidScopes;
        }

        /**
         * @return the validScopes
         */
        public Set<ScopeView> getValidScopes() {
            return validScopes;
        }

        /**
         * @return the invalidScopes
         */
        public Set<ScopeView> getInvalidScopes() {
            return invalidScopes;
        }


        public static final ScopeSet empty() {
            return empty;
        }
    }
}
