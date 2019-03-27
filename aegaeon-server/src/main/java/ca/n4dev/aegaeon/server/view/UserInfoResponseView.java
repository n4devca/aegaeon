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
package ca.n4dev.aegaeon.server.view;

import java.util.Map;

import ca.n4dev.aegaeon.api.token.payload.Claims;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserInfoResponse.java
 * 
 * A response to userinfo endpoint.
 *
 * @author by rguillemette
 * @since Jul 31, 2017
 */
public class UserInfoResponseView {
   
    private String sub;
    
//    private String name;
//
//    private String email;
//
//    private String picture;
//
//    @JsonProperty("zoneinfo")
//    private String zoneInfo;
//
//    private String locale;
//
//    @JsonProperty("preferred_username")
//    private String preferredUsername;

    private Map<String, Object> payload;
    
    public UserInfoResponseView(String pSub, Map<String, Object> pPayload) {
        this.sub = pSub;
        
//        this.name = (String) pPayload.get(Claims.NAME);
//        this.email = (String) pPayload.get(Claims.EMAIL);
//        this.picture = (String) pPayload.get(Claims.PICTURE);
//        this.zoneInfo = (String) pPayload.get(Claims.ZONEINFO);
//        this.locale = (String) pPayload.get(Claims.LOCALE);
//        this.preferredUsername = (String) pPayload.get(Claims.USERNAME);
        this.payload = pPayload;
    }


    /**
     * @return the sub
     */
    public String getSub() {
        return sub;
    }

    @JsonAnyGetter
    public Map<String, Object> getPayload() {

        return payload;
    }

//    /**
//     * @return the name
//     */
//    public String getName() {
//        return name;
//    }
//
//
//    /**
//     * @return the email
//     */
//    public String getEmail() {
//        return email;
//    }
//
//
//    /**
//     * @return the picture
//     */
//    public String getPicture() {
//        return picture;
//    }
//
//
//    /**
//     * @return the zoneInfo
//     */
//    public String getZoneInfo() {
//        return zoneInfo;
//    }
//
//
//    /**
//     * @return the locale
//     */
//    public String getLocale() {
//        return locale;
//    }


}
