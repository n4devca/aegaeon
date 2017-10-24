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

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.TokenProviderType;
import ca.n4dev.aegaeon.api.token.payload.Claims;
import ca.n4dev.aegaeon.server.config.ServerInfo;

/**
 * BaseJwtVerifier.java
 * 
 * Base verifier using nimbus.
 *
 * @author by rguillemette
 * @since Jul 19, 2017
 */
public abstract class BaseJwtVerifier {
    
    protected ServerInfo serverInfo;
    protected TokenProviderType tokenProviderType;
    
    public BaseJwtVerifier(TokenProviderType pTokenProviderType, ServerInfo pServerInfo) {
        this.tokenProviderType = pTokenProviderType;
        this.serverInfo = pServerInfo;
    }
    
    protected abstract JWSVerifier getJWSVerifier();

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#validate(java.lang.String)
     */
    public boolean validate(String pToken) {
        
        try {
            SignedJWT signedJWT = SignedJWT.parse(pToken);
            return signedJWT.verify(getJWSVerifier()) 
                    && signedJWT.getJWTClaimsSet().getIssuer().equals(
                            this.serverInfo.getIssuer());
        } catch (Exception e) {
            // ignore
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#extract(java.lang.String)
     */
    public OAuthUser extract(String pToken) {
        try {

            SignedJWT signedJWT = SignedJWT.parse(pToken);
            return extract(signedJWT);
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#extractAndValidate(java.lang.String)
     */
    public OAuthUser extractAndValidate(String pToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(pToken);
            
            if (signedJWT.verify(getJWSVerifier()) 
                    && signedJWT.getJWTClaimsSet().getIssuer().equals(
                            this.serverInfo.getIssuer())) {
                
                return extract(signedJWT);
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#getVerifierName()
     */
    public String getVerifierName() {
        return this.tokenProviderType.getTypeName();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#getType()
     */
    public TokenProviderType getType() {
        return this.tokenProviderType;
    }

    private OAuthUser extract(SignedJWT pSignedJWT) {
        try {
            
            JWTClaimsSet claims = pSignedJWT.getJWTClaimsSet();
            
            final String sub = claims.getSubject();
            final String name = claims.getStringClaim(Claims.NAME);
            
            return new OAuthUser() {
                
                @Override
                public String getUniqueIdentifier() {
                    return sub;
                }
                
                @Override
                public String getName() {
                    return name;
                }
                
                @Override
                public Long getId() {
                    return null;
                }
            };
            
        } catch (Exception e) {
            // ignore
        }
        
        return null;
    }
}
