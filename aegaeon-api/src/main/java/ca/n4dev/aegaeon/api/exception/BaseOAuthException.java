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

import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * BaseOAuthException.java
 * 
 * A base class to all errors.
 *
 * @author by rguillemette
 * @since May 24, 2017
 */
public class BaseOAuthException extends ServerException {

    private static final long serialVersionUID = 3676269645498864982L;

    protected Class<?> source;
    
    protected String userId;
    
    protected OpenIdErrorType error;
    
    @JsonIgnore
    protected AuthRequest authRequest;
    
    @JsonProperty("error_description")
    protected String errorDescription;
    
    @JsonProperty("error_uri")
    protected String errorUri;
    
    @JsonIgnore
    protected String redirectUrl;
    
    @JsonIgnore
    protected String clientPublicId;

    public BaseOAuthException(Class<?> pSource, OpenIdErrorType pError) {
        this(pSource, pError, null);
    }


    public BaseOAuthException(Class<?> pSource, OpenIdErrorType pError, AuthRequest pAuthRequest) {
        this.source = pSource;
        this.error = pError;
        this.authRequest = pAuthRequest;
    }


    /**
     * @return This error as String.
     */
    public String toString() {

        return new StringBuilder()
                .append(authRequest.getResponseType())
                .append(",")
                .append(this.error)
                .toString();
    }

    /**
     * @return the error
     */
    public OpenIdErrorType getError() {
        return error;
    }

    /**
     * @param pError the error to set
     */
    public void setError(OpenIdErrorType pError) {
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
     * @return the authRequest
     */
    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    /**
     * @param pAuthRequest the authRequest to set
     */
    public void setAuthRequest(AuthRequest pAuthRequest) {
        authRequest = pAuthRequest;
    }
}
