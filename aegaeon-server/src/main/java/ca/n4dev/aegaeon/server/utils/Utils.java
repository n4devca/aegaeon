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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * ObjectUtils.java
 * 
 * Static functions to check various state of object (empty, null, etc)
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public class Utils {

    public static boolean isEmpty(String pValue) {
        return pValue == null || pValue.isEmpty();
    }
    
    public static boolean areOneEmpty(String... pValues) {
        
        if (pValues != null) {
            
            for (String v : pValues) {
                if (isEmpty(v)) {
                    return true;
                }
            }
            
            return false;
        }
        
        return true;
    }
    
    public static boolean isNotEmpty(String pValue) {
        return pValue != null && !pValue.isEmpty();
    }
    
    public static boolean isStillValid(LocalDateTime pValidUntil) {
        if (pValidUntil != null) {
            LocalDateTime now = LocalDateTime.now();
            return pValidUntil.isBefore(now);
        }
        
        return true;
    }
    
    public static <E> String join(String pSeparator, List<E> pElements, Function<E, String> pFunc) {
        if (pElements == null || pElements.isEmpty()) {
            return "";
        }
        
        StringBuilder b = new StringBuilder();
        boolean first = true;
        
        for (E e : pElements) {
            if (!first) 
                b.append(pSeparator);
            
            b.append(pFunc.apply(e));
            first = false;
        }
        
        return b.toString();
    }
    
    public static <E> List<E> explode(String pSeparator, String pElementsStr, Function<String, E> pFunc) {
        if (pElementsStr == null || pElementsStr.isEmpty()) {
            return Collections.emptyList();
        }
        
        String[] els = pElementsStr.split(pSeparator);
        List<E> lst = new ArrayList<>();
        
        for (String e : els) {
            if (isNotEmpty(e)) {
                lst.add(pFunc.apply(e));
            }
        }
        
        return lst;
    }
}
