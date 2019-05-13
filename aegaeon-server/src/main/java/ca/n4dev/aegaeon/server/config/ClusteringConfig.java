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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * ClusteringConfig.java
 *
 * Configuration class to clustering with redis.
 *
 * @author by rguillemette
 * @since May 10, 2019
 */
@Configuration
@ConditionalOnProperty(prefix = "aegaeon.modules",
                       name = "clustering",
                       havingValue = "true", matchIfMissing = false)
public class ClusteringConfig {

    @Value("${aegaeon.config.redis.host:localhost}")
    private String redisHost;

    @Value("${aegaeon.config.redis.port:6379}")
    private int redisPort;

    @Value("${aegaeon.config.redis.password:}")
    private String redisPassword;

    /**
     * @return A connection factory for lettuce / redis.
     */
    @Bean("lettuceConnectionFactory")
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration();

        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(redisPassword);

        return new LettuceConnectionFactory(config);
    }
}
