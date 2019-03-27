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
package ca.n4dev.aegaeon.api.token.payload;

/**
 * Claims.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 13, 2017
 */
public class Claims {
    
    private Claims() {}
    
    public static final String SUB = "sub";
    public static final String NAME = "name";
    public static final String USERNAME = "preferred_username";
    public static final String PICTURE = "picture";
    public static final String WEBSITE = "website";
    public static final String ADDRESS = "address";
    public static final String EMAIL = "email";
    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String PHONE_NUMBER_VERIFIED = "phone_number_verified";
    public static final String UPDATED_AT = "updated_at";
    public static final String LOCALE = "locale";
    public static final String ZONEINFO = "zoneinfo";
    public static final String NONCE = "nonce";
    
}
