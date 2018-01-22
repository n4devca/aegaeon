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
package ca.n4dev.aegaeon.api.exception;

/**
 * ServerExceptionCode.java
 * 
 * A code describing every ServerException.
 *
 * @author by rguillemette
 * @since Jun 4, 2017
 */
public enum ServerExceptionCode {
    USER_EMPTY,
    CLIENT_UNAUTHORIZED,
    CLIENT_EMPTY,
    CLIENT_REDIRECTURL_EMPTY,
    SCOPE_INVALID,
    SCOPE_UNAUTHORIZED,
    SCOPE_UNAUTHORIZED_OFFLINE,

    INVALID_PARAMETER,

    ENTITY_ID_EMPTY,
    ENTITY_EMPTY,

    CLIENT_ATTR_EMPTY,
    CLIENT_ATTR_INVALID,
    CLIENT_DUPLICATE_PUBLICID,
    CLIENT_REDIRECTIONURL_INVALID,

    UNEXPECTED_ERROR,

    USER_INVALID_PASSWORD,
    ;
}
