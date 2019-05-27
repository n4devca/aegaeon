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

import java.util.function.Supplier;

import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Assert.java
 *
 * Object used to check various condition and throw an Exception
 * if the condition is not met.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
public class Assert {

    private static final Logger LOGGER = LoggerFactory.getLogger(Assert.class);

    @Deprecated
    public static void notNull(Object pObj, ServerExceptionCode pCode) {
        if (pObj == null) {
            Utils.raise(pCode);
        }
    }

    @Deprecated
    public static void notNull(Object pObj, ServerExceptionCode pCode, String pLogMsg) {
        if (pObj == null) {
            Utils.raise(pCode, pLogMsg);
        }
    }

    @Deprecated
    public static void notEmpty(String pObj, ServerExceptionCode pCode) {
        if (pObj == null || pObj.isEmpty()) {
            Utils.raise(pCode);
        }
    }

    public static void notNull(Object pObj, Supplier<RuntimeException> pRuntimeExceptionSupplier) {
        if (pObj == null) {
            throw pRuntimeExceptionSupplier.get();
        }
    }

    public static void notEmpty(String pObj, Supplier<RuntimeException> pRuntimeExceptionSupplier) {
        if (pObj == null || pObj.isEmpty() || pObj.trim().isEmpty()) {
            throw pRuntimeExceptionSupplier.get();
        }
    }

    public static void isEmpty(String pObj, Supplier<RuntimeException> pRuntimeExceptionSupplier) {
        if (pObj != null && !pObj.isEmpty()) {
            throw pRuntimeExceptionSupplier.get();
        }
    }

    public static void isTrue(Boolean pValue, Supplier<RuntimeException> pRuntimeExceptionSupplier) {
        if (pValue == null || !pValue) {
            throw pRuntimeExceptionSupplier.get();
        }
    }

    public static void equals(String pValue1, String pValue2, Supplier<RuntimeException> pRuntimeExceptionSupplier) {
        if (!Utils.equals(pValue1, pValue2)) {
            throw pRuntimeExceptionSupplier.get();
        }
    }

}
