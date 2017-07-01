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
package ca.n4dev.aegaeon.api.protocol;

/**
 * FlowFactory.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 27, 2017
 */
public class FlowFactory {
    private FlowFactory() {}
    

    public static Flow of(String pCode) {
        Flow f = new Flow();
        f.setResponseType(new String[] {pCode});
        
        if (pCode.equals(Flow.CODE)) {
            f.getRequestedGrant().add(RequestedGrant.AUTHORIZATIONCODE);
        } else if (pCode.equals(Flow.IMPLICIT) || pCode.equals(Flow.ID_TOKEN)) {
            f.getRequestedGrant().add(RequestedGrant.IMPLICIT);
        } else if (pCode.equals(Flow.CLIENTCREDENTIALS)) { // OAuth
            f.getRequestedGrant().clear();
            f.getRequestedGrant().add(RequestedGrant.CLIENTCREDENTIALS);
        } else if (pCode.equals(Flow.REFRESH_TOKEN)) { // OAuth
            f.getRequestedGrant().clear();
            f.getRequestedGrant().add(RequestedGrant.REFRESH_TOKEN);
        }
        
        return f;
    }
    
    public static Flow implicit() {
        return of(Flow.IMPLICIT);
    }
    
    public static Flow authCode() {
        return of(Flow.CODE);
    }
    
    public static Flow clientCredential() {
        return of(Flow.CLIENTCREDENTIALS);
    }
    
    public static Flow refreshToken() {
        return of(Flow.REFRESH_TOKEN);
    }
}
