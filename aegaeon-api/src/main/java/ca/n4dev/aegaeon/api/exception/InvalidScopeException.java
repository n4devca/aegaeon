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

import java.util.ArrayList;
import java.util.List;

/**
 * InvalidScopeException.java
 * 
 * Throwed when one or many scope are invalid.
 *
 * @author by rguillemette
 * @since May 28, 2017
 */
public class InvalidScopeException extends ServerException {

    /**
     * 
     */
    private static final long serialVersionUID = -3951149025139631227L;

    private List<String> invalidScopes = new ArrayList<>();

    /**
     * @return the invalidScopes
     */
    public List<String> getInvalidScopes() {
        return invalidScopes;
    }

    /**
     * @param pInvalidScopes the invalidScopes to set
     */
    public void setInvalidScopes(List<String> pInvalidScopes) {
        invalidScopes = pInvalidScopes;
    }
    
    /**
     * @param pInvalidScopes the invalidScope to add
     */
    public void addInvalidScope(String pInvalidScope) {
        invalidScopes.add(pInvalidScope);
    }
}
