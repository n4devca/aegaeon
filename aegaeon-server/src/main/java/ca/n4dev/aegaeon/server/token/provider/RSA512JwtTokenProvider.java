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
import java.util.Map;
import java.util.Map.Entry;

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
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.token.key.KeysProvider;

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
    
    private boolean enable = false;
    
    
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
            
            if (jwk.isPrivate()) {
                
                if (jwk instanceof RSAKey) {
                    keyId = jwk.getKeyID();
                    // Create Signers
                    this.rsaSigner = new RSASSASigner((RSAKey) jwk);
                    enable = true;
                    break;
                } 
            }
        }
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#getProviderName()
     */
    @Override
    public String getAlgorithmName() {
        return JWSAlgorithm.RS512.toString();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#createToken(ca.n4dev.aegaeon.server.token.OAuthUser, ca.n4dev.aegaeon.server.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        return createToken(pOAuthUser, pOAuthClient, pTimeValue, pTemporalUnit, Collections.emptyMap());
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

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#isEnable()
     */
    @Override
    public boolean isEnabled() {
        return this.enable;
    }
    
    /*
     * 
    iss
        REQUIRED. Issuer Identifier for the Issuer of the response. The iss value is a case sensitive URL using the https scheme that contains scheme, host, and optionally, port number and path components and no query or fragment components. 
    sub
        REQUIRED. Subject Identifier. A locally unique and never reassigned identifier within the Issuer for the End-User, which is intended to be consumed by the Client, e.g., 24400320 or AItOawmwtWwcT0k51BayewNvutrJUqsvl6qs7A4. It MUST NOT exceed 255 ASCII characters in length. The sub value is a case sensitive string. 
    aud
        REQUIRED. Audience(s) that this ID Token is intended for. It MUST contain the OAuth 2.0 client_id of the Relying Party as an audience value. It MAY also contain identifiers for other audiences. In the general case, the aud value is an array of case sensitive strings. In the common special case when there is one audience, the aud value MAY be a single case sensitive string. 
    exp
        REQUIRED. Expiration time on or after which the ID Token MUST NOT be accepted for processing. The processing of this parameter requires that the current date/time MUST be before the expiration date/time listed in the value. Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time. See RFC 3339 [RFC3339] for details regarding date/times in general and UTC in particular. 
    iat
        REQUIRED. Time at which the JWT was issued. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time. 
    auth_time
        Time when the End-User authentication occurred. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time. When a max_age request is made or when auth_time is requested as an Essential Claim, then this Claim is REQUIRED; otherwise, its inclusion is OPTIONAL. (The auth_time Claim semantically corresponds to the OpenID 2.0 PAPE [OpenID.PAPE] auth_time response parameter.) 
    nonce
        String value used to associate a Client session with an ID Token, and to mitigate replay attacks. The value is passed through unmodified from the Authentication Request to the ID Token. If present in the ID Token, Clients MUST verify that the nonce Claim Value is equal to the value of the nonce parameter sent in the Authentication Request. If present in the Authentication Request, Authorization Servers MUST include a nonce Claim in the ID Token with the Claim Value being the nonce value sent in the Authentication Request. Authorization Servers SHOULD perform no other processing on nonce values used. The nonce value is a case sensitive string. 
    acr
        OPTIONAL. Authentication Context Class Reference. String specifying an Authentication Context Class Reference value that identifies the Authentication Context Class that the authentication performed satisfied. The value "0" indicates the End-User authentication did not meet the requirements of ISO/IEC 29115 [ISO29115] level 1. Authentication using a long-lived browser cookie, for instance, is one example where the use of "level 0" is appropriate. Authentications with level 0 SHOULD NOT be used to authorize access to any resource of any monetary value. (This corresponds to the OpenID 2.0 PAPE [OpenID.PAPE] nist_auth_level 0.) An absolute URI or an RFC 6711 [RFC6711] registered name SHOULD be used as the acr value; registered names MUST NOT be used with a different meaning than that which is registered. Parties using this claim will need to agree upon the meanings of the values used, which may be context-specific. The acr value is a case sensitive string. 
    amr
        OPTIONAL. Authentication Methods References. JSON array of strings that are identifiers for authentication methods used in the authentication. For instance, values might indicate that both password and OTP authentication methods were used. The definition of particular values to be used in the amr Claim is beyond the scope of this specification. Parties using this claim will need to agree upon the meanings of the values used, which may be context-specific. The amr value is an array of case sensitive strings. 
    azp
        OPTIONAL. Authorized party - the party to which the ID Token was issued. If present, it MUST contain the OAuth 2.0 Client ID of this party. This Claim is only needed when the ID Token has a single audience value and that audience is different than the authorized party. It MAY be included even when the authorized party is the same as the sole audience. The azp value is a case sensitive string containing a StringOrURI value. 
        
     * */

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit, java.util.List)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, 
                             OAuthClient pOAuthClient, 
                             Long pTimeValue, 
                             TemporalUnit pTemporalUnit,
                             Map<String, String> pPayloads) throws Exception {
        
        LocalDateTime expiredIn = LocalDateTime.now().plus(pTimeValue, pTemporalUnit);
        Instant instant = expiredIn.toInstant(ZoneOffset.UTC);
        Date date = Date.from(instant);
        
        Builder builder = new JWTClaimsSet.Builder();
        
        builder.expirationTime(date);        
        builder.issuer(this.serverInfo.getIssuer());
        builder.subject(pOAuthUser.getUniqueIdentifier());
        builder.audience(pOAuthClient.getClientId());
        
        if (pPayloads != null && !pPayloads.isEmpty()) {
            for (Entry<String, String> en : pPayloads.entrySet()) {
                builder.claim(en.getKey(), en.getValue());
            }
        }
        
        JWTClaimsSet claimsSet = builder.build();
        
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claimsSet);
        signedJWT.sign(rsaSigner);

        Token token = new Token(signedJWT.serialize(), expiredIn);
        
        return token;
    }
}
