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
package ca.n4dev.aegaeon.server.config;

/**
 * ServerInfo.java
 * 
 * Represent the informations of this server.
 *
 * @author by rguillemette
 * @since May 18, 2017
 */
public class ServerInfo {

    
    private String issuer;
    
    private String logoUrl;
    
    private String legalEntity;

    private String privacyPolicy;
    
    private String customStyleSheet;

	public ServerInfo() {}
    
    public ServerInfo(String pIssuer) {
        this(pIssuer, null, null, null, null);
    }
    
    public ServerInfo(String pIssuer, String pLegalEntity, String pLogoUrl, String pPrivacyPolicy, String pCustomStyleSheet) {
        
        if (pIssuer == null || pIssuer.isEmpty()) {
            throw new RuntimeException("Issuer is mandatory");
        }
        
        this.issuer = pIssuer;
        this.logoUrl = pLogoUrl;
        this.legalEntity = pLegalEntity;
        this.privacyPolicy = pPrivacyPolicy;
        this.customStyleSheet = pCustomStyleSheet;
    }
    

    /**
     * @return the legalEntity
     */
    public String getLegalEntity() {
        return legalEntity;
    }

    /**
     * @param pLegalEntity the legalEntity to set
     */
    public void setLegalEntity(String pLegalEntity) {
        legalEntity = pLegalEntity;
    }

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
     * @return the logoUrl
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param pLogoUrl the logoUrl to set
     */
    public void setLogoUrl(String pLogoUrl) {
        logoUrl = pLogoUrl;
    }

    /**
     * @return the privacyPolicy
     */
    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    /**
     * @param pPrivacyPolicy the privacyPolicy to set
     */
    public void setPrivacyPolicy(String pPrivacyPolicy) {
        privacyPolicy = pPrivacyPolicy;
    }
    

    /**
	 * @return the customStyleSheet
	 */
	public String getCustomStyleSheet() {
		return customStyleSheet;
	}

	/**
	 * @param customStyleSheet the customStyleSheet to set
	 */
	public void setCustomStyleSheet(String customStyleSheet) {
		this.customStyleSheet = customStyleSheet;
	}
}
