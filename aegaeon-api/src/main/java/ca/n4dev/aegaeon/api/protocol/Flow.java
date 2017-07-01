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

import java.util.Set;

/**
 * Flow.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 22, 2017
 */
public class Flow {

    public static final String CODE = "code";
    public static final String IMPLICIT = "token";
    public static final String ID_TOKEN = "id_token";
    public static final String CLIENTCREDENTIALS = "client_credentials";
    public static final String REFRESH_TOKEN = "refresh_token";
    
    private Set<RequestedGrant> requestedGrant;
    
    private String[] responseType;
    
    public Flow() {}
    
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
    protected void parse() {
        if (this.responseType != null && this.responseType.length > 0) {
            
            for (String r : this.responseType) {
                if (r.equals(CODE)) {
                    this.requestedGrant.add(RequestedGrant.AUTHORIZATIONCODE);
                } else if (r.equals(IMPLICIT) || r.equals(ID_TOKEN)) {
                    this.requestedGrant.add(RequestedGrant.IMPLICIT);
                } else if (r.equals(CLIENTCREDENTIALS)) { // OAuth
                    this.requestedGrant.clear();
                    this.requestedGrant.add(RequestedGrant.CLIENTCREDENTIALS);
                } else if (r.equals(REFRESH_TOKEN)) { // OAuth
                    this.requestedGrant.clear();
                    this.requestedGrant.add(RequestedGrant.REFRESH_TOKEN);
                }
            }
        }
    }

    public boolean has(RequestedGrant pGrant) {
        for (RequestedGrant rg : this.requestedGrant) {
            if (rg == pGrant) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return the requestedGrant
     */
    public Set<RequestedGrant> getRequestedGrant() {
        return requestedGrant;
    }

    /**
     * @param pRequestedGrant the requestedGrant to set
     */
    public void setRequestedGrant(Set<RequestedGrant> pRequestedGrant) {
        requestedGrant = pRequestedGrant;
    }

    /**
     * @return the responseType
     */
    public String[] getResponseType() {
        return responseType;
    }

    /**
     * @param pResponseType the responseType to set
     */
    public void setResponseType(String[] pResponseType) {
        responseType = pResponseType;
    }
    
    public String toString() {
        StringBuilder b = new StringBuilder();
        
        for (RequestedGrant g : this.requestedGrant) {
            if (b.length() > 0) {
                b.append(" ");
            }
            b.append(g.toString());
        }
        
        return b.toString();            
    }
    
    public static final Flow of(String pCode) {
        return of(pCode.split(" "));
    }
    
    public static final Flow of(String[] pCode) {
        
        Flow f = new Flow();
        f.responseType = pCode;
        
        if (pCode.equals(CODE)) {
            f.requestedGrant.add(RequestedGrant.AUTHORIZATIONCODE);
        } else if (pCode.equals(IMPLICIT) || pCode.equals(ID_TOKEN)) {
            f.requestedGrant.add(RequestedGrant.IMPLICIT);
        } else if (pCode.equals(CLIENTCREDENTIALS)) { // OAuth
            f.requestedGrant.clear();
            f.requestedGrant.add(RequestedGrant.CLIENTCREDENTIALS);
        } else if (pCode.equals(REFRESH_TOKEN)) { // OAuth
            f.requestedGrant.clear();
            f.requestedGrant.add(RequestedGrant.REFRESH_TOKEN);
        }
        
        return f;
    }
    
}
