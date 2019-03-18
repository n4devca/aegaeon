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
package ca.n4dev.aegaeon.server.token.provider;

import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import org.springframework.stereotype.Component;

/**
 * RSA512JwtTokenProvider.java
 * 
 * A TokenProvider producing RSA (512) token.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
@Component
public class RSA512JwtTokenProvider extends BaseRSAJwtTokenProvider {

    /**
     * Default Constructor.
     *
     * @param pKeysProvider The key store of the server.
     * @param pServerInfo
     * @throws JOSEException
     */
    public RSA512JwtTokenProvider(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        super(pKeysProvider, pServerInfo);
    }

    @Override
    protected JWSAlgorithm getJWSAlgorithm() {
        return JWSAlgorithm.RS512;
    }

    @Override
    public TokenProviderType getType() {
        return TokenProviderType.RSA_RS512;
    }
}
