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
package ca.n4dev.aegaeon.server.caching;

import ca.n4dev.aegaeon.server.service.ClientService;
import ca.n4dev.aegaeon.server.service.ScopeService;
import ca.n4dev.aegaeon.server.token.TokenFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * LocalCacheManager.java
 *
 * Configure a CacheManager for each cache name.
 *
 *
 * @author by rguillemette
 * @since May 10, 2019
 */
public class LocalCacheManager implements CacheManager {

    private Map<String, Cache> caches;

    public LocalCacheManager() {
        Map<String, Cache> cs = new HashMap<>();

        // Caches
        cs.put(ScopeService.CACHE_NAME, scopeServiceCache());
        cs.put(TokenFactory.CACHE_NAME, tokenFactoryCache());
        cs.put(ClientService.CACHE_NAME, clientServiceCache());

        caches = Collections.unmodifiableMap(cs);
    }


    @Override
    public Cache getCache(String pCacheName) {
        return caches.get(pCacheName);
    }

    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }

    private Cache scopeServiceCache() {
        return new ConcurrentMapCache(ScopeService.CACHE_NAME);
    }

    private Cache tokenFactoryCache() {
        return new ConcurrentMapCache(TokenFactory.CACHE_NAME);
    }

    private Cache clientServiceCache() {
        return new ConcurrentMapCache(ClientService.CACHE_NAME);
    }
}
