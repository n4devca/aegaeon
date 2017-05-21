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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

import ca.n4dev.aegaeon.server.token.key.KeysProvider;

/**
 * JwtTokenVerifier.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
@Component
public class JwtTokenVerifier implements TokenVerifier {

    private Map<String, JWSVerifier> verifiers;
    
    /**
     * @throws JOSEException 
     * 
     */
    @Autowired
    public JwtTokenVerifier(KeysProvider pKeysProvider) throws JOSEException {
        JWKSet keySet = pKeysProvider.getJwkSet();
        
        // Create Signers
        this.verifiers = new LinkedHashMap<>();
        
        for (JWK jwk : keySet.getKeys()) {
            String id = jwk.getKeyID();
            
            if (jwk instanceof RSAKey) {
                
                RSASSAVerifier rsaVerifier = new RSASSAVerifier((RSAKey) jwk);
                this.verifiers.put(id, rsaVerifier);
                
            } else if (jwk instanceof ECKey) {
                
//                ECDSASigner signer = new ECDSASigner((ECKey) jwk);
//                this.signers.put(id, signer);
                
            } else if (jwk instanceof OctetSequenceKey) {
                
//                MACSigner signer = new MACSigner((OctetSequenceKey) jwk);
//                this.signers.put(id, signer);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.verifier.TokenVerifier#verify(com.nimbusds.jose.JWSObject)
     */
    @Override
    public boolean verify(JWSObject pJWSObject) {
        // TODO Auto-generated method stub
        return false;
    }

}
