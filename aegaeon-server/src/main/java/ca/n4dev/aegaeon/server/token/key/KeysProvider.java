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
package ca.n4dev.aegaeon.server.token.key;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * KeysProvider.java
 * 
 * Read keys from jndi and store them.
 *
 * @author by rguillemette
 * @since May 15, 2017
 */
@Component
public class KeysProvider {
    
    private final JWKSet jwkSet;
    
    /**
     * Default Constructor.
     * @param pObjectMapper Jackson's ObjectMapper 
     */
    @Autowired
    public KeysProvider(@Value("${aegaeon.jwks}") String pKeyUri) throws Exception {
        this.jwkSet = JWKSet.load(new File(pKeyUri));
    }

    
    public JWK getKeyById(String pId) {
        for (JWK j : this.jwkSet.getKeys()) {
            if (j.getKeyID().equals(pId)) {
                return j;
            }
        }
        return null;
    }


    /**
     * @return the jwkSet
     */
    public JWKSet getJwkSet() {
        return jwkSet;
    }
}
