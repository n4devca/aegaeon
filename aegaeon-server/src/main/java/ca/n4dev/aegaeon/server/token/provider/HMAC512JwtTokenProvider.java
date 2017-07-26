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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;

/**
 * HMAC512JwtTokenProvider.java
 * 
 * A TokenProvider producing HMAC token.
 *
 * @author by rguillemette
 * @since Jun 6, 2017
 */
@Service
public class HMAC512JwtTokenProvider implements TokenProvider {

    private String keyId;
    private ServerInfo serverInfo;
    private JWSSigner signer;
    private boolean enabled = false;
    
    /**
     * @throws JOSEException 
     * 
     */
    @Autowired
    public HMAC512JwtTokenProvider(KeysProvider pKeysProvider, ServerInfo pServerInfo) throws JOSEException {
        this.serverInfo = pServerInfo;
        
        JWKSet keySet = pKeysProvider.getJwkSet();
        
        for (JWK jwk : keySet.getKeys()) {
            
            if (jwk.isPrivate()) {
                
                if (jwk instanceof OctetSequenceKey) {
                    keyId = jwk.getKeyID();
                    this.signer = new MACSigner((OctetSequenceKey) jwk);
                    break;
                }
            }
        }
        
        if (this.signer != null) {
            this.enabled = true;
        }
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#getProviderName()
     */
    @Override
    public String getProviderName() {
        return TokenProviderType.HMAC_HS512.getTypeName();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#getType()
     */
    @Override
    public TokenProviderType getType() {
        return TokenProviderType.HMAC_HS512;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        return createToken(pOAuthUser, pOAuthClient, pTimeValue, pTemporalUnit, Collections.emptyMap());
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @return the keyId
     */
    public String getKeyId() {
        return keyId;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit, java.util.List)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit,
            Map<String, String> pPayloads) throws Exception {
        
        LocalDateTime expiredIn = LocalDateTime.now().plus(pTimeValue, pTemporalUnit);
        Instant instant = expiredIn.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);

        Builder builder = new JWTClaimsSet.Builder();
        
        builder.expirationTime(date);        
        builder.issuer(this.serverInfo.getIssuer());
        builder.subject(pOAuthUser.getUniqueIdentifier());
        builder.audience(pOAuthClient.getClientId());
        
        JWTClaimsSet claimsSet = builder.build();
        
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
        signedJWT.sign(this.signer);

        Token token = new Token(signedJWT.serialize(), expiredIn);
        
        return token;
    }

}
