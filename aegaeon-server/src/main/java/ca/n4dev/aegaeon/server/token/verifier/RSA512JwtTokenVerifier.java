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
package ca.n4dev.aegaeon.server.token.verifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.verifier.TokenVerifier;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;

/**
 * RSA512JwtTokenVerifier.java
 * 
 * RSA / 512 JWT verifier.
 *
 * @author by rguillemette
 * @since Jul 18, 2017
 */
@Component
public class RSA512JwtTokenVerifier extends BaseJwtVerifier implements TokenVerifier {

    private boolean enable = false;
    
    private JWSVerifier verifier = null;
    
    @Autowired
    public RSA512JwtTokenVerifier(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        super(TokenProviderType.RSA_RS512, pServerInfo);
        
        JWKSet keySet = pKeysProvider.getJwkSet();
        
        for (JWK jwk : keySet.getKeys()) {
            
            if (jwk instanceof RSAKey) {
                this.verifier = new RSASSAVerifier((RSAKey) jwk);
                this.enable = true;
                break;
            } 
        }
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#isEnable()
     */
    @Override
    public boolean isEnable() {
        return this.enable;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.verifier.BaseJwtVerifier#getJWSVerifier()
     */
    @Override
    protected JWSVerifier getJWSVerifier() {
        return this.verifier;
    }
}
