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
	
    public static final String PARAM_CODE = "code";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_ID_TOKEN = "id_token";
    public static final String PARAM_CLIENTCREDENTIALS = "client_credentials";
    public static final String PARAM_REFRESH_TOKEN = "refresh_token";
    
    private FlowFactory() {}
    

    /**
     * Parse a response_type param and determine the Flow to follow.
     * 
     * Rules are:  
     * code: authorization_code
     * id_token: implicit
     * token id_token: implicit
     * code id_token: authorization_code, implicit
     * code token: authorization_code, implicit
     * code token id_token: authorization_code, implicit
     */
    public static Flow of(String[] pCode) {
        return of(pCode, null);
    }
    
    public static Flow of(String[] pCode, String pNonce) {
        
    	Flow f = new Flow();
    	
    	f.setNonce(pNonce);
    	f.setResponseType(pCode);
    	
    	if (f.getResponseType() != null && f.getResponseType().length > 0) {
    		
    		for (String r : f.getResponseType()) {
    			if (r.equals(PARAM_CODE)) {
    				f.getRequestedGrant().add(GrantType.AUTHORIZATION_CODE);
    			} else if (r.equals(PARAM_TOKEN) || r.equals(PARAM_ID_TOKEN)) {
    				f.getRequestedGrant().add(GrantType.IMPLICIT);
    			} else if (r.equals(PARAM_CLIENTCREDENTIALS)) { // OAuth
    				f.getRequestedGrant().clear();
    				f.getRequestedGrant().add(GrantType.CLIENT_CREDENTIALS);
    			} else if (r.equals(PARAM_REFRESH_TOKEN)) { // OAuth
    				f.getRequestedGrant().clear();
    				f.getRequestedGrant().add(GrantType.REFRESH_TOKEN);
    			}
    		}
    	}
    	
    	return f;
    }

    public static Flow of(String pCode) {
        return of(pCode, null);
    }
            
    public static Flow of(String pCode, String pNonce) {
        if (pCode != null) {

            if (pCode.indexOf(" ") != -1) {
                return of(pCode.split(" "), pNonce);
            } else {
                return of(new String[] {pCode}, pNonce);
            }
        }

        return null;
    }
    
    public static Flow implicit() {
        return of(new String[] {PARAM_ID_TOKEN, PARAM_TOKEN}, null);
    }
    
    public static Flow authCode() {
        return of(PARAM_CODE, null);
    }
    
    public static Flow clientCredential() {
        return of(PARAM_CLIENTCREDENTIALS, null);
    }
    
    public static Flow refreshToken() {
        return of(PARAM_REFRESH_TOKEN, null);
    }
}
