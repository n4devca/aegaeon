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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.verifier.TokenVerifier;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;

/**
 * HMAC512JwtTokenVerifier.java
 * 
 * HMAC / 512 JWT verifier.
 *
 * @author by rguillemette
 * @since Jul 19, 2017
 */
public class HMAC512JwtTokenVerifier extends BaseJwtVerifier implements TokenVerifier {
    
    private boolean enable = false;
    
    private JWSVerifier verifier = null;
    
    public HMAC512JwtTokenVerifier(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        super(TokenProviderType.HMAC_HS512, pServerInfo);
        
        JWKSet keySet = pKeysProvider.getJwkSet();
        
        for (JWK jwk : keySet.getKeys()) {
            
            if (jwk instanceof OctetSequenceKey) {
                this.verifier = new MACVerifier(((OctetSequenceKey) jwk).toByteArray());
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
