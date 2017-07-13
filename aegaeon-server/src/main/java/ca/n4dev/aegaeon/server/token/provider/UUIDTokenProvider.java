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

import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import ca.n4dev.aegaeon.api.token.OAuthClient;
import ca.n4dev.aegaeon.api.token.OAuthUser;
import ca.n4dev.aegaeon.api.token.Token;
import ca.n4dev.aegaeon.api.token.provider.TokenProvider;
import ca.n4dev.aegaeon.api.token.provider.TokenProviderType;

/**
 * UUIDTokenProvider.java
 * 
 * A simple tokenprovider (mainly for test) creating a Token from a uuid.
 *
 * @author by rguillemette
 * @since May 13, 2017
 */
@Component
public class UUIDTokenProvider implements TokenProvider {


    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#getProviderName()
     */
    @Override
    public String getProviderName() {
        return TokenProviderType.UUID.getTypeName();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#createToken(ca.n4dev.aegaeon.server.token.OAuthUser, 
     *      ca.n4dev.aegaeon.server.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, OAuthClient pOAuthClient, Long pTimeValue, TemporalUnit pTemporalUnit) throws Exception {
        return createToken(pOAuthUser, pOAuthClient, pTimeValue, pTemporalUnit, null);
    }
    

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.TokenProvider#getType()
     */
    @Override
    public TokenProviderType getType() {
        return TokenProviderType.UUID;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit, java.util.List)
     */
    @Override
    public Token createToken(OAuthUser pOAuthUser, 
                             OAuthClient pOAuthClient, 
                             Long pTimeValue, 
                             TemporalUnit pTemporalUnit,
                             Map<String, String> pPayloads) throws Exception {
        return new Token(UUID.randomUUID().toString(), pTimeValue, pTemporalUnit);
    }

}
