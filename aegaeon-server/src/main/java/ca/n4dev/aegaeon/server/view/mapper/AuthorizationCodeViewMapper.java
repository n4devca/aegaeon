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

import ca.n4dev.aegaeon.api.model.AuthorizationCode;
import ca.n4dev.aegaeon.server.view.AuthorizationCodeView;
import org.springframework.stereotype.Component;

/**
 * AuthorizationCodeViewMapper.java
 * <p>
 * AuthorizationCodeView mapper.
 *
 * @author by rguillemette
 * @since Dec 10, 2017
 */
@Component
public class AuthorizationCodeViewMapper {

    public AuthorizationCodeView toView(AuthorizationCode pAuthorizationCode) {
        AuthorizationCodeView authorizationCodeView = new AuthorizationCodeView();

        if (pAuthorizationCode != null) {

            if (pAuthorizationCode.getClient() != null) {
                authorizationCodeView.setClientId(pAuthorizationCode.getClient().getPublicId());
            }

            if (pAuthorizationCode.getUser() != null) {
                authorizationCodeView.setUserName(pAuthorizationCode.getUser().getUserName());
            }

            authorizationCodeView.setCode(pAuthorizationCode.getCode());
        }

        return authorizationCodeView;
    }
}
