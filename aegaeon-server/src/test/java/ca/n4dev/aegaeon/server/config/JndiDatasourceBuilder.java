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
package ca.n4dev.aegaeon.server.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * DatasourceConfig.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 29, 2017
 */
public class JndiDatasourceBuilder {
    private static final String USERNAME = "aegaeon";
    private static final String PASSWORD = "aegaeon";
    private static final String JNDI_NAME = "aegaeon";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/aegaeon";
    private static DataSource ds;

    public static void create() throws Exception {
        final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();

        ds = DataSourceBuilder
                .create()
                .username(USERNAME)
                .password(PASSWORD)
                .url(URL)
                .driverClassName(DRIVER)
                .build();

        builder.bind("java:comp/env/jdbc/" + JNDI_NAME, ds);
        builder.activate();
    }
}
