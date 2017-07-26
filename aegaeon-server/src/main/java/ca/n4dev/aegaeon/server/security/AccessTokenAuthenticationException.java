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
package ca.n4dev.aegaeon.server.security;

import org.springframework.security.core.AuthenticationException;

/**
 * AccessTokenAuthenticationException.java
 * 
 * AuthenticationException throwed during Access Token Auth filter.
 *
 * @author by rguillemette
 * @since Jul 18, 2017
 */
public class AccessTokenAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1022833597003808371L;


    /**
     * Constructor to use with message.
     * @param pMessage The exception message.
     */
    public AccessTokenAuthenticationException(String pMessage) {
        super(pMessage);
    }

    /**
     * Constructor to use with message and exception.
     * @param pMessage The exception message.
     * @param pException The exception to wrap.
     */
    public AccessTokenAuthenticationException(String pMessage, Exception pException) {
        super(pMessage, pException);
    }
    
    /**
     * Constructor to use with exception.
     * @param pException The exception to wrap.
     */
    public AccessTokenAuthenticationException(Exception pException) {
        super(pException.getMessage(), pException);
    }
}
