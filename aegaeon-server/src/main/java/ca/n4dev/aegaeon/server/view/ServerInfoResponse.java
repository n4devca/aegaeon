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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ServerInfoResponse.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jul 26, 2017
 */
public class ServerInfoResponse {

    /*
     * "issuer":
     "https://server.example.com",
   "authorization_endpoint":
     "https://server.example.com/connect/authorize",
   "token_endpoint":
     "https://server.example.com/connect/token",
   "token_endpoint_auth_methods_supported":
     ["client_secret_basic", "private_key_jwt"],
   "token_endpoint_auth_signing_alg_values_supported":
     ["RS256", "ES256"],
   "userinfo_endpoint":
     "https://server.example.com/connect/userinfo",
   "check_session_iframe":
     "https://server.example.com/connect/check_session",
   "end_session_endpoint":
     "https://server.example.com/connect/end_session",
   "jwks_uri":
     "https://server.example.com/jwks.json",
   "registration_endpoint":
     "https://server.example.com/connect/register",
   "scopes_supported":
     ["openid", "profile", "email", "address",
      "phone", "offline_access"],
   "response_types_supported":
     ["code", "code id_token", "id_token", "token id_token"],
   "acr_values_supported":
     ["urn:mace:incommon:iap:silver",
      "urn:mace:incommon:iap:bronze"],
   "subject_types_supported":
     ["public", "pairwise"],
   "userinfo_signing_alg_values_supported":
     ["RS256", "ES256", "HS256"],
   "userinfo_encryption_alg_values_supported":
     ["RSA1_5", "A128KW"],
   "userinfo_encryption_enc_values_supported":
     ["A128CBC-HS256", "A128GCM"],
   "id_token_signing_alg_values_supported":
     ["RS256", "ES256", "HS256"],
   "id_token_encryption_alg_values_supported":
     ["RSA1_5", "A128KW"],
   "id_token_encryption_enc_values_supported":
     ["A128CBC-HS256", "A128GCM"],
   "request_object_signing_alg_values_supported":
     ["none", "RS256", "ES256"],
   "display_values_supported":
     ["page", "popup"],
   "claim_types_supported":
     ["normal", "distributed"],
   "claims_supported":
     ["sub", "iss", "auth_time", "acr",
      "name", "given_name", "family_name", "nickname",
      "profile", "picture", "website",
      "email", "email_verified", "locale", "zoneinfo",
      "http://example.info/claims/groups"],
   "claims_parameter_supported":
     true,
   "service_documentation":
     "http://server.example.com/connect/service_documentation.html",
   "ui_locales_supported":
     ["en-US", "en-GB", "en-CA", "fr-FR", "fr-CA"]
     * */
    
    private String issuer;
    
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    
    @JsonProperty("token_endpoint_auth_methods_supported")
    private List<String> tokenEndpointAuthMethodsSupported;
    
    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    private List<String> tokenEndpointAuthSigningAlgValuesSupported;
    
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;
    
    @JsonProperty("check_session_iframe")
    private String checkSessionIframe;
    
    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;
    
    @JsonProperty("jwks_uri")
    private String jwksUri;
    
    @JsonProperty("registration_endpoint")
    private String registrationEndpoint;
    
    @JsonProperty("scopes_supported")
    private List<String> scopesSupported;
    
    @JsonProperty("response_types_supported")
    private List<String> responseTypesSupported;
    
    @JsonProperty("acr_values_supported")
    private List<String> acrValuesSupported;
    
    @JsonProperty("subject_types_supported")
    private List<String> subjectTypesSupported;
    
    @JsonProperty("userinfo_signing_alg_values_supported")
    private List<String> userinfoSigningAlgValuesSupported;
    
    @JsonProperty("userinfo_encryption_alg_values_supported")
    private List<String> userinfoEncryptionAlgValuesSupported;
    
    @JsonProperty("userinfo_encryption_enc_values_supported")
    private List<String> userinfoEncryptionEncValuesSupported;
    
    @JsonProperty("id_token_signing_alg_values_supported")
    private List<String> idTokenSigningAlgValuesSupported;
    
    @JsonProperty("id_token_encryption_alg_values_supported")
    private List<String> idTokenEncryptionAlgValuesSupported;
    
    @JsonProperty("id_token_encryption_enc_values_supported")
    private List<String> idTokenEncryptionEncValuesSupported;
    
    @JsonProperty("request_object_signing_alg_values_supported")
    private List<String> requestObjectSigningAlgValuesSupported;
    
    @JsonProperty("display_values_supported")
    private List<String> displayValuesSupported;
    
    @JsonProperty("claim_types_supported")
    private List<String> claimTypesSupported;
    
    @JsonProperty("claims_supported")
    private List<String> claimsSupported;
    
    @JsonProperty("claims_parameter_supported")
    private boolean claimsParameterSupported;
    
    @JsonProperty("service_documentation")
    private String serviceDocumentation;
    
    @JsonProperty("ui_locales_supported")
    private List<String> uiLocalesSupported;
    
    @JsonProperty("op_policy_uri")
    private String opPolicyUri;
    
    @JsonProperty("op_tos_uri")
    private String opTosUri;

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @param pIssuer the issuer to set
     */
    public void setIssuer(String pIssuer) {
        issuer = pIssuer;
    }

    /**
     * @return the authorizationEndpoint
     */
    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    /**
     * @param pAuthorizationEndpoint the authorizationEndpoint to set
     */
    public void setAuthorizationEndpoint(String pAuthorizationEndpoint) {
        authorizationEndpoint = pAuthorizationEndpoint;
    }

    /**
     * @return the tokenEndpoint
     */
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    /**
     * @param pTokenEndpoint the tokenEndpoint to set
     */
    public void setTokenEndpoint(String pTokenEndpoint) {
        tokenEndpoint = pTokenEndpoint;
    }

    /**
     * @return the tokenEndpointAuthMethodsSupported
     */
    public List<String> getTokenEndpointAuthMethodsSupported() {
        return tokenEndpointAuthMethodsSupported;
    }

    /**
     * @param pTokenEndpointAuthMethodsSupported the tokenEndpointAuthMethodsSupported to set
     */
    public void setTokenEndpointAuthMethodsSupported(List<String> pTokenEndpointAuthMethodsSupported) {
        tokenEndpointAuthMethodsSupported = pTokenEndpointAuthMethodsSupported;
    }

    /**
     * @return the tokenEndpointAuthSigningAlgValuesSupported
     */
    public List<String> getTokenEndpointAuthSigningAlgValuesSupported() {
        return tokenEndpointAuthSigningAlgValuesSupported;
    }

    /**
     * @param pTokenEndpointAuthSigningAlgValuesSupported the tokenEndpointAuthSigningAlgValuesSupported to set
     */
    public void setTokenEndpointAuthSigningAlgValuesSupported(List<String> pTokenEndpointAuthSigningAlgValuesSupported) {
        tokenEndpointAuthSigningAlgValuesSupported = pTokenEndpointAuthSigningAlgValuesSupported;
    }

    /**
     * @return the userinfoEndpoint
     */
    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    /**
     * @param pUserinfoEndpoint the userinfoEndpoint to set
     */
    public void setUserinfoEndpoint(String pUserinfoEndpoint) {
        userinfoEndpoint = pUserinfoEndpoint;
    }

    /**
     * @return the checkSessionIframe
     */
    public String getCheckSessionIframe() {
        return checkSessionIframe;
    }

    /**
     * @param pCheckSessionIframe the checkSessionIframe to set
     */
    public void setCheckSessionIframe(String pCheckSessionIframe) {
        checkSessionIframe = pCheckSessionIframe;
    }

    /**
     * @return the endSessionEndpoint
     */
    public String getEndSessionEndpoint() {
        return endSessionEndpoint;
    }

    /**
     * @param pEndSessionEndpoint the endSessionEndpoint to set
     */
    public void setEndSessionEndpoint(String pEndSessionEndpoint) {
        endSessionEndpoint = pEndSessionEndpoint;
    }

    /**
     * @return the jwksUri
     */
    public String getJwksUri() {
        return jwksUri;
    }

    /**
     * @param pJwksUri the jwksUri to set
     */
    public void setJwksUri(String pJwksUri) {
        jwksUri = pJwksUri;
    }

    /**
     * @return the registrationEndpoint
     */
    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    /**
     * @param pRegistrationEndpoint the registrationEndpoint to set
     */
    public void setRegistrationEndpoint(String pRegistrationEndpoint) {
        registrationEndpoint = pRegistrationEndpoint;
    }

    /**
     * @return the scopesSupported
     */
    public List<String> getScopesSupported() {
        return scopesSupported;
    }

    /**
     * @param pScopesSupported the scopesSupported to set
     */
    public void setScopesSupported(List<String> pScopesSupported) {
        scopesSupported = pScopesSupported;
    }

    /**
     * @return the responseTypesSupported
     */
    public List<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }

    /**
     * @param pResponseTypesSupported the responseTypesSupported to set
     */
    public void setResponseTypesSupported(List<String> pResponseTypesSupported) {
        responseTypesSupported = pResponseTypesSupported;
    }

    /**
     * @return the acrValuesSupported
     */
    public List<String> getAcrValuesSupported() {
        return acrValuesSupported;
    }

    /**
     * @param pAcrValuesSupported the acrValuesSupported to set
     */
    public void setAcrValuesSupported(List<String> pAcrValuesSupported) {
        acrValuesSupported = pAcrValuesSupported;
    }

    /**
     * @return the subjectTypesSupported
     */
    public List<String> getSubjectTypesSupported() {
        return subjectTypesSupported;
    }

    /**
     * @param pSubjectTypesSupported the subjectTypesSupported to set
     */
    public void setSubjectTypesSupported(List<String> pSubjectTypesSupported) {
        subjectTypesSupported = pSubjectTypesSupported;
    }

    /**
     * @return the userinfoSigningAlgValuesSupported
     */
    public List<String> getUserinfoSigningAlgValuesSupported() {
        return userinfoSigningAlgValuesSupported;
    }

    /**
     * @param pUserinfoSigningAlgValuesSupported the userinfoSigningAlgValuesSupported to set
     */
    public void setUserinfoSigningAlgValuesSupported(List<String> pUserinfoSigningAlgValuesSupported) {
        userinfoSigningAlgValuesSupported = pUserinfoSigningAlgValuesSupported;
    }

    /**
     * @return the userinfoEncryptionAlgValuesSupported
     */
    public List<String> getUserinfoEncryptionAlgValuesSupported() {
        return userinfoEncryptionAlgValuesSupported;
    }

    /**
     * @param pUserinfoEncryptionAlgValuesSupported the userinfoEncryptionAlgValuesSupported to set
     */
    public void setUserinfoEncryptionAlgValuesSupported(List<String> pUserinfoEncryptionAlgValuesSupported) {
        userinfoEncryptionAlgValuesSupported = pUserinfoEncryptionAlgValuesSupported;
    }

    /**
     * @return the userinfoEncryptionEncValuesSupported
     */
    public List<String> getUserinfoEncryptionEncValuesSupported() {
        return userinfoEncryptionEncValuesSupported;
    }

    /**
     * @param pUserinfoEncryptionEncValuesSupported the userinfoEncryptionEncValuesSupported to set
     */
    public void setUserinfoEncryptionEncValuesSupported(List<String> pUserinfoEncryptionEncValuesSupported) {
        userinfoEncryptionEncValuesSupported = pUserinfoEncryptionEncValuesSupported;
    }

    /**
     * @return the idTokenSigningAlgValuesSupported
     */
    public List<String> getIdTokenSigningAlgValuesSupported() {
        return idTokenSigningAlgValuesSupported;
    }

    /**
     * @param pIdTokenSigningAlgValuesSupported the idTokenSigningAlgValuesSupported to set
     */
    public void setIdTokenSigningAlgValuesSupported(List<String> pIdTokenSigningAlgValuesSupported) {
        idTokenSigningAlgValuesSupported = pIdTokenSigningAlgValuesSupported;
    }

    /**
     * @return the idTokenEncryptionAlgValuesSupported
     */
    public List<String> getIdTokenEncryptionAlgValuesSupported() {
        return idTokenEncryptionAlgValuesSupported;
    }

    /**
     * @param pIdTokenEncryptionAlgValuesSupported the idTokenEncryptionAlgValuesSupported to set
     */
    public void setIdTokenEncryptionAlgValuesSupported(List<String> pIdTokenEncryptionAlgValuesSupported) {
        idTokenEncryptionAlgValuesSupported = pIdTokenEncryptionAlgValuesSupported;
    }

    /**
     * @return the idTokenEncryptionEncValuesSupported
     */
    public List<String> getIdTokenEncryptionEncValuesSupported() {
        return idTokenEncryptionEncValuesSupported;
    }

    /**
     * @param pIdTokenEncryptionEncValuesSupported the idTokenEncryptionEncValuesSupported to set
     */
    public void setIdTokenEncryptionEncValuesSupported(List<String> pIdTokenEncryptionEncValuesSupported) {
        idTokenEncryptionEncValuesSupported = pIdTokenEncryptionEncValuesSupported;
    }

    /**
     * @return the requestObjectSigningAlgValuesSupported
     */
    public List<String> getRequestObjectSigningAlgValuesSupported() {
        return requestObjectSigningAlgValuesSupported;
    }

    /**
     * @param pRequestObjectSigningAlgValuesSupported the requestObjectSigningAlgValuesSupported to set
     */
    public void setRequestObjectSigningAlgValuesSupported(List<String> pRequestObjectSigningAlgValuesSupported) {
        requestObjectSigningAlgValuesSupported = pRequestObjectSigningAlgValuesSupported;
    }

    /**
     * @return the displayValuesSupported
     */
    public List<String> getDisplayValuesSupported() {
        return displayValuesSupported;
    }

    /**
     * @param pDisplayValuesSupported the displayValuesSupported to set
     */
    public void setDisplayValuesSupported(List<String> pDisplayValuesSupported) {
        displayValuesSupported = pDisplayValuesSupported;
    }

    /**
     * @return the claimTypesSupported
     */
    public List<String> getClaimTypesSupported() {
        return claimTypesSupported;
    }

    /**
     * @param pClaimTypesSupported the claimTypesSupported to set
     */
    public void setClaimTypesSupported(List<String> pClaimTypesSupported) {
        claimTypesSupported = pClaimTypesSupported;
    }

    /**
     * @return the claimsSupported
     */
    public List<String> getClaimsSupported() {
        return claimsSupported;
    }

    /**
     * @param pClaimsSupported the claimsSupported to set
     */
    public void setClaimsSupported(List<String> pClaimsSupported) {
        claimsSupported = pClaimsSupported;
    }

    /**
     * @return the claimsParameterSupported
     */
    public boolean isClaimsParameterSupported() {
        return claimsParameterSupported;
    }

    /**
     * @param pClaimsParameterSupported the claimsParameterSupported to set
     */
    public void setClaimsParameterSupported(boolean pClaimsParameterSupported) {
        claimsParameterSupported = pClaimsParameterSupported;
    }

    /**
     * @return the serviceDocumentation
     */
    public String getServiceDocumentation() {
        return serviceDocumentation;
    }

    /**
     * @param pServiceDocumentation the serviceDocumentation to set
     */
    public void setServiceDocumentation(String pServiceDocumentation) {
        serviceDocumentation = pServiceDocumentation;
    }

    /**
     * @return the uiLocalesSupported
     */
    public List<String> getUiLocalesSupported() {
        return uiLocalesSupported;
    }

    /**
     * @param pUiLocalesSupported the uiLocalesSupported to set
     */
    public void setUiLocalesSupported(List<String> pUiLocalesSupported) {
        uiLocalesSupported = pUiLocalesSupported;
    }

    /**
     * @return the opPolicyUri
     */
    public String getOpPolicyUri() {
        return opPolicyUri;
    }

    /**
     * @param pOpPolicyUri the opPolicyUri to set
     */
    public void setOpPolicyUri(String pOpPolicyUri) {
        opPolicyUri = pOpPolicyUri;
    }

    /**
     * @return the opTosUri
     */
    public String getOpTosUri() {
        return opTosUri;
    }

    /**
     * @param pOpTosUri the opTosUri to set
     */
    public void setOpTosUri(String pOpTosUri) {
        opTosUri = pOpTosUri;
    }
    
    
}
