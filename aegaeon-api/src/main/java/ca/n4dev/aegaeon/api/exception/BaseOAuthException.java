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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ca.n4dev.aegaeon.api.protocol.Flow;

/**
 * BaseOAuthException.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 24, 2017
 */
public abstract class BaseOAuthException extends ServerException {

    private static final long serialVersionUID = 3676269645498864982L;

    protected Class<?> source;
    
    protected String userId;
    
    protected OAuthErrorType error;
    
    @JsonIgnore
    protected Flow flow;
    
    @JsonProperty("error_description")
    protected String errorDescription;
    
    @JsonProperty("error_uri")
    protected String errorUri;
    
    @JsonIgnore
    protected String redirectUrl;
    
    @JsonIgnore
    protected String clientPublicId;
    
    public BaseOAuthException(Class<?> pSource, Flow pFlow, OAuthErrorType pError) {
        this.error = pError;
        this.flow = pFlow;
    }
    

    public String toString() {
        return new StringBuilder()
                        .append(this.flow.toString())
                        .append(",")
                        .append(this.error)
                        .toString();
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
     * @return the redirectUrl
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * @param pRedirectUrl the redirectUrl to set
     */
    public void setRedirectUrl(String pRedirectUrl) {
        redirectUrl = pRedirectUrl;
    }

    /**
     * @return the clientPublicId
     */
    public String getClientPublicId() {
        return clientPublicId;
    }

    /**
     * @param pClientPublicId the clientPublicId to set
     */
    public void setClientPublicId(String pClientPublicId) {
        clientPublicId = pClientPublicId;
    }

    /**
     * @return the source
     */
    public Class<?> getSource() {
        return source;
    }

    /**
     * @param pSource the source to set
     */
    public void setSource(Class<?> pSource) {
        source = pSource;
    }


    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }


    /**
     * @param pUserId the userId to set
     */
    public void setUserId(String pUserId) {
        userId = pUserId;
    }


    /**
     * @return the flow
     */
    public Flow getFlow() {
        return flow;
    }


    /**
     * @param pFlow the flow to set
     */
    public void setFlow(Flow pFlow) {
        flow = pFlow;
    }

}
