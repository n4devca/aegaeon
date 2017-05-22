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
package ca.n4dev.aegaeon.server.controller;

import org.springframework.web.servlet.view.RedirectView;

import ca.n4dev.aegaeon.server.exception.OAuthErrorType;
import ca.n4dev.aegaeon.server.exception.OAuthPublicException;

/**
 * BaseController.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public abstract class BaseController {
    
    protected static final String HASHTAG = "#";

    protected RedirectView handleOAuthException(String pClientRedirectUrl) {
        OAuthPublicException o = new OAuthPublicException(OAuthErrorType.server_error);
        return handleOAuthException(pClientRedirectUrl, o);
    }
    
    // We can either redirect to the client or show an error to the user
    // if the client is not authorize or setup correctly.
    
    
    
    protected RedirectView handleOAuthException(String pClientRedirectUrl, OAuthPublicException pOAuthServerException) {
        String param = pOAuthServerException.asUri();
        return new RedirectView(pClientRedirectUrl + HASHTAG + param);
    }
}
