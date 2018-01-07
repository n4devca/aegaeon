/*
 * *
 *  * Copyright 2017 Remi Guillemette - n4dev.ca
 *  *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 *
 */

package ca.n4dev.aegaeon.api.protocol;

import ca.n4dev.aegaeon.api.token.TokenProviderType;

public class ClientConfig {

    public static final TokenProviderType DEFAULT_PROVIDER_TYPE = TokenProviderType.RSA_RS512;
    public static final Long DEFAULT_ID_TOKEN_SECONDS = 600L; // 10 minute
    public static final Long DEFAULT_ACCESS_TOKEN_SECONDS = 3600L; // 1 hour
    public static final Long DEFAULT_REFRESH_TOKEN_SECONDS = 604800L; // 7 days
    public static final Long MAX_REFRESH_TOKEN_SECONDS = 31536000L; // 1 year


}
