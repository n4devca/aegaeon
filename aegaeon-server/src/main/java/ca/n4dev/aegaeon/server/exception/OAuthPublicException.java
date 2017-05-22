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
package ca.n4dev.aegaeon.server.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuthServerException.java
 * 
 * OAuth error throwed by the token or authorize endpoint.
 * 
 * https://tools.ietf.org/html/rfc6749#section-4.2.2.1
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public class OAuthPublicException extends RuntimeException {

    private static final long serialVersionUID = 2546692690614294789L;

    private OAuthErrorType error;
    
    @JsonProperty("error_description")
    private String errorDescription;
    
    @JsonProperty("error_uri")
    private String errorUri;
    
    private String state;
    
    /**
     * Empty Constructor.
     */
    public OAuthPublicException() {}
    
    /**
     * Create an OAuthServerException with an error code.
     * @param pError The error code.
     */
    public OAuthPublicException(OAuthErrorType pError) {
        this.error = pError;
    }
    

    /**
     * @return the error
     */
    public OAuthErrorType getError() {
        return error;
    }

    /**
     * @param pError the error to set
     */
    public void setError(OAuthErrorType pError) {
        error = pError;
    }

    /**
     * @return the errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * @param pErrorDescription the errorDescription to set
     */
    public void setErrorDescription(String pErrorDescription) {
        errorDescription = pErrorDescription;
    }

    /**
     * @return the errorUri
     */
    public String getErrorUri() {
        return errorUri;
    }

    /**
     * @param pErrorUri the errorUri to set
     */
    public void setErrorUri(String pErrorUri) {
        errorUri = pErrorUri;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param pState the state to set
     */
    public void setState(String pState) {
        state = pState;
    }
    
    @JsonIgnore
    public String asUri() {
        StringBuilder b = new StringBuilder();
        
        // TODO(RG) : check characters to be compliant
        appendNotNull(b, "error", this.error != null ? this.error.toString() : null);
        appendNotNull(b, "state", this.state);
        appendNotNull(b, "error_description", this.errorDescription);
        appendNotNull(b, "error_uri", this.errorUri);
        
        return b.toString();
    }
    
    private void appendNotNull(StringBuilder pBuilder, String pKey, String pValue) {
        

        if (pKey != null && !pKey.isEmpty() && pValue != null && !pValue.isEmpty()) {
            
            if (pBuilder.length() > 0) {
                pBuilder.append("&");
            }

            pBuilder.append(pKey).append("=").append(pValue);
        }
    }
}
