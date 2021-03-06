/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.api.token;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

    private ZonedDateTime validUntil;

    private TokenType type;

    /**
     * Default Constructor.
     */
    public Token() {
    }

    /**
     * Create a simple token. Default type is ACCESS TOKEN.
     * @param pValue The token value.
     */
    public Token(String pValue, ZonedDateTime pValidUntil) {
        Assert.hasLength(pValue, "A token cannot have an empty value.");
        this.value = pValue;
        this.validUntil = pValidUntil != null ? pValidUntil : ZonedDateTime.now(ZoneOffset.UTC);
        this.type = TokenType.ACCESS_TOKEN;
    }

    /**
     * Create a simple token.
     * @param pValue The token value.
     */
    public Token(String pValue, Long pTimeValue, TemporalUnit pTemporalUnit) {

        Assert.notNull(pTimeValue, "The time value cannot be null");
        Assert.notNull(pTemporalUnit, "The temporal unit value cannot be null");

        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);

        this.value = pValue;
        this.validUntil = time.plus(pTimeValue, pTemporalUnit);
    }

    /**
     * Create a simple token.
     * @param pValue The token value.
     * @param pValidUntil Is valid until this date.
     * @param pTokenType The type of token.
     */
    public Token(String pValue, ZonedDateTime pValidUntil, TokenType pTokenType) {
        Assert.hasLength(pValue, "A token cannot have an empty value.");
        this.value = pValue;
        this.validUntil = pValidUntil != null ? pValidUntil : ZonedDateTime.now(ZoneOffset.UTC);
        this.type = pTokenType;
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
    public ZonedDateTime getValidUntil() {
        return validUntil;
    }

    /**
     * @param pValidUntil the validUntil to set
     */
    public void setValidUntil(ZonedDateTime pValidUntil) {
        validUntil = pValidUntil != null ? pValidUntil : ZonedDateTime.now(ZoneOffset.UTC);
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
