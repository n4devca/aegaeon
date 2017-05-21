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
package ca.n4dev.aegaeon.api.token;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

import org.springframework.util.Assert;

/**
 * Token.java
 * 
 * Hold a token, a type and a date until it is valid.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
public class Token {
    
    private String value;
    
    private LocalDateTime validUntil;
    
    private TokenType type;
    
    /**
     * Default Constructor.
     */
    public Token() {}
    
    /**
     * Create a simple token.
     * @param pValue The token value.
     */
    public Token(String pValue, LocalDateTime pValidUntil) {
        Assert.hasLength(pValue, "A token cannot have an empty value.");
        this.value = pValue;
        this.validUntil = pValidUntil != null ? pValidUntil : LocalDateTime.now() ;
    }
    
    /**
     * Create a simple token.
     * @param pValue The token value.
     */
    public Token(String pValue, Long pTimeValue, TemporalUnit pTemporalUnit) {
        
        Assert.notNull(pTimeValue, "The time value cannot be null");
        Assert.notNull(pTemporalUnit, "The temporal unit value cannot be null");
        
        LocalDateTime time = LocalDateTime.now();
        
        this.value = pValue;
        this.validUntil = time.plus(pTimeValue, pTemporalUnit);;
    }
    

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param pValue the value to set
     */
    public void setValue(String pValue) {
        Assert.hasLength(pValue, "A token cannot have an empty value.");
        value = pValue;
    }

    /**
     * @return the validUntil
     */
    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    /**
     * @param pValidUntil the validUntil to set
     */
    public void setValidUntil(LocalDateTime pValidUntil) {
        validUntil = pValidUntil != null ? pValidUntil : LocalDateTime.now();
    }

    /**
     * @return the type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * @param pType the type to set
     */
    public void setType(TokenType pType) {
        type = pType;
    }
    
    
}
