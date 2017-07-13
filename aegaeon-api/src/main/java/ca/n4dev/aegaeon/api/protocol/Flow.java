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

import java.util.HashSet;
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

    
    private Set<RequestedGrant> requestedGrant;
    
    private String[] responseType;
    
    public Flow() {
    	this.requestedGrant = new HashSet<>();
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
    
}
