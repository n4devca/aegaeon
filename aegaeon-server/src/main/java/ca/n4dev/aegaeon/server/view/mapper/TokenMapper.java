/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.view.mapper;

import ca.n4dev.aegaeon.api.model.AccessToken;
import ca.n4dev.aegaeon.api.model.BaseTokenEntity;
import ca.n4dev.aegaeon.api.model.IdToken;
import ca.n4dev.aegaeon.api.model.RefreshToken;
import ca.n4dev.aegaeon.server.view.TokenView;
import org.springframework.stereotype.Component;

/**
 * TokenMapper.java
 *
 * Mapper for tokens.
 *
 * @author by rguillemette
 * @since Dec 12, 2017
 */
@Component
public class TokenMapper {


    public TokenView toView(BaseTokenEntity pTokenEntity) {
        TokenView tokenView = new TokenView();

        tokenView.setId(pTokenEntity.getId());
        tokenView.setToken(pTokenEntity.getToken());
        tokenView.setTokenType(pTokenEntity.getTokenType().toString());
        tokenView.setScopes(pTokenEntity.getScopes());
        tokenView.setValidUntil(pTokenEntity.getValidUntil());

        return tokenView;
    }


    public AccessToken toAccessToken(TokenView pTokenView) {
        return toTokenEntity(pTokenView, new AccessToken());
    }


    public IdToken toIdToken(TokenView pTokenView) {
        return toTokenEntity(pTokenView, new IdToken());
    }


    public RefreshToken toRefreshToken(TokenView pTokenView) {
        return toTokenEntity(pTokenView, new RefreshToken());
    }

    private <T extends BaseTokenEntity> T toTokenEntity(TokenView pTokenView, T pTokenEntity) {

        pTokenEntity.setId(pTokenView.getId());
        pTokenEntity.setToken(pTokenView.getToken());
        pTokenEntity.setScopes(pTokenView.getScopes());
        pTokenEntity.setValidUntil(pTokenView.getValidUntil());

        return pTokenEntity;
    }
}
