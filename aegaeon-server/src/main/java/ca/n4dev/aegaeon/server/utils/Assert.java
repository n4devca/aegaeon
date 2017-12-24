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
package ca.n4dev.aegaeon.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;

/**
 * Assert.java
 * 
 * Object used to check various condition and throw a ServerException 
 * if the condition is not met.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
public class Assert {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Assert.class);
    
    public static void notNull(Object pObj, ServerExceptionCode pCode) {
        if (pObj == null) {
            throw new ServerException(pCode);
        }
    }
    
    public static void notNull(Object pObj, ServerExceptionCode pCode, String pLogMsg) {
        if (pObj == null) {
            
            if (pLogMsg != null) {
                LOGGER.warn(pLogMsg);
            }
            
            throw new ServerException(pCode);
        }
    }
    
    public static void notEmpty(String pObj, ServerExceptionCode pCode) {
        if (pObj == null || pObj.isEmpty()) {
            throw new ServerException(pCode);
        }
    }
    
    public static void isTrue(Boolean pValue, ServerExceptionCode pCode) {
        if (pValue == null || !pValue) {
            throw new ServerException(pCode);
        }
    }
    
    public static void isFalse(Boolean pValue, ServerExceptionCode pCode) {
        if (pValue == null || pValue) {
            throw new ServerException(pCode);
        }
    }
}
