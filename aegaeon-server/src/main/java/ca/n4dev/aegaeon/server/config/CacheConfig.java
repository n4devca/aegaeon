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
package ca.n4dev.aegaeon.server.config;

import ca.n4dev.aegaeon.server.caching.LocalCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


/**
 * CacheConfig.java
 *
 * Configuration class to manage cache.
 *
 * @author by rguillemette
 * @since May 10, 2019
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired(required = false)
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    @ConditionalOnProperty(prefix = "aegaeon.modules", name = "clustering", havingValue = "false", matchIfMissing = true)
    public CacheManager localCacheManager() {
        return new LocalCacheManager();
    }

    /**
     * Depends on ClusteringConfig
     * @return A cache manager backed by Redis.
     */
    @Bean
    @ConditionalOnProperty(prefix = "aegaeon.modules", name = "clustering", havingValue = "true", matchIfMissing = false)
    public CacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager.create(lettuceConnectionFactory);
        redisCacheManager.setTransactionAware(true);
        return redisCacheManager;
    }

}
