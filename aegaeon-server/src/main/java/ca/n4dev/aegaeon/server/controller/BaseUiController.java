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
package ca.n4dev.aegaeon.server.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;

import ca.n4dev.aegaeon.server.utils.Utils;

/**
 * BaseController.java
 * 
 * A base controller to expose MessageSource.
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public abstract class BaseUiController {
    
    private MessageSource messages;
    
    /**
     * Build this constructor with a label message source.
     * @param pMessages The message source.
     */
    public BaseUiController(MessageSource pMessages) {
        this.messages = pMessages;
    }

    /**
     * Get a translated label from the proper bundle.
     * 
     * @param pKey The label key
     * @param pLocale the locale
     * @return A String.
     */
    protected String getLabel(String pKey, Locale pLocale) {
        return getLabel(pKey, null, pLocale);
    }
    
    /**
     * Get a translated label from the proper bundle.
     * 
     * @param pKey The label key
     * @param pParameters the parameters
     * @param pLocale the locale
     * @return A String.
     */
    protected String getLabel(String pKey, Object[] pParameters, Locale pLocale) {
        if (Utils.isNotEmpty(pKey)) {
            return messages.getMessage(pKey.toLowerCase(), pParameters, pLocale);            
        }
        return null;
    }
}
