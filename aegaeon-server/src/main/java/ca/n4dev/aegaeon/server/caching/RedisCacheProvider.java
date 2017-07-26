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
package ca.n4dev.aegaeon.server.caching;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import ca.n4dev.aegaeon.api.caching.CacheProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisCacheProvider.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 21, 2017
 */
@Component
@ConditionalOnProperty(prefix = "aegaeon.features.caching", name = "redis", havingValue = "true", matchIfMissing = false)
public class RedisCacheProvider implements CacheProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheProvider.class);
    
    private final JedisPool jedisPool;
    
    public RedisCacheProvider(String pRedisHost) {
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        
        this.jedisPool = new JedisPool(poolConfig, pRedisHost);
    }
    

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.caching.CacheProvider#get(java.lang.String, java.lang.String)
     */
    @Override
    public String get(String pNameSpace, String pKey) {

        try (Jedis j = this.jedisPool.getResource()) {
            
            return j.get(pNameSpace);
        } catch (Exception e) {
            LOGGER.error("RedisCacheProvider#get", e);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.caching.CacheProvider#set(java.lang.String, java.lang.String, int, java.lang.Object)
     */
    @Override
    public void set(String pNameSpace, String pKey, int pTTL, String pEntity) {
        try (Jedis j = this.jedisPool.getResource()) {
            
            j.set(pKey, pEntity);
            
        } catch (Exception e) {
            LOGGER.error("RedisCacheProvider#set", e);
        }
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.caching.CacheProvider#remove(java.lang.String, java.lang.String)
     */
    @Override
    public void remove(String pNameSpace, String pKey) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.caching.CacheProvider#clear(java.lang.String)
     */
    @Override
    public void clear(String pNameSpace) {
        // TODO Auto-generated method stub
        
    }

}
