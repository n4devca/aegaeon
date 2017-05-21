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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.api.token.provider.TokenProviderType;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;
import ca.n4dev.aegaeon.server.utils.Assert;

/**
 * JwtTokenProvider.java
 * 
 * A JWT token provider using nimbus library.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
@Component
public class RSA512JwtTokenProvider implements TokenProvider {

    private String keyId = null;
    
    private RSASSASigner rsaSigner = null;
    
    private ServerInfo serverInfo;
    
    
    /**
     * Default Constructor.
     * @param pKeyStore The key store of the server.
     * @throws JOSEException 
     */
    @Autowired
    public RSA512JwtTokenProvider(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        this.serverInfo = pServerInfo;
        JWKSet keySet = pKeysProvider.getJwkSet();
        
        
        
        for (JWK jwk : keySet.getKeys()) {
            keyId = jwk.getKeyID();
            
            if (jwk.isPrivate()) {
                
                if (jwk instanceof RSAKey) {
                    // Create Signers
                    this.rsaSigner = new RSASSASigner((RSAKey) jwk);
                } 
                /*
                else if (jwk instanceof ECKey) {
                    
                    ECDSASigner signer = new ECDSASigner((ECKey) jwk);
                    this.signers.put(id, signer);
                    
                } else if (jwk instanceof OctetSequenceKey) {
                    
                    MACSigner signer = new MACSigner((OctetSequenceKey) jwk);
                    this.signers.put(id, signer);
                }
                */
            }
        }
        
        Assert.notNull(rsaSigner, "Did not find RSA key in your JWK key set.");
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#getProviderName()
     */
    @Override
    public String getProviderName() {
        return TokenProviderType.RSA_RS512.getTypeName();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#createToken(ca.n4dev.aegaeon.server.token.OAuthUser, ca.n4dev.aegaeon.server.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        
        LocalDateTime expiredIn = LocalDateTime.now().plus(pTimeValue, pTemporalUnit);
        Instant instant = expiredIn.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        
        Builder builder = new JWTClaimsSet.Builder();
        
        builder.expirationTime(date);        
        builder.issuer(this.serverInfo.getIssuer());
        builder.subject(pOAuthUser.getUniqueIdentifier());
        builder.audience(pOAuthClient.getClientId());
        
        JWTClaimsSet claimsSet = builder.build();
        
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claimsSet);
        signedJWT.sign(rsaSigner);

        Token token = new Token(signedJWT.serialize(), expiredIn);
        
        return token;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#getType()
     */
    @Override
    public TokenProviderType getType() {
        return TokenProviderType.RSA_RS512;
    }

    /**
     * @return the keyId
     */
    public String getKeyId() {
        return keyId;
    }
}
