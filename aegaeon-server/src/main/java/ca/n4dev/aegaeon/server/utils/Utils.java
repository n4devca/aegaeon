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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ca.n4dev.aegaeon.api.exception.ServerException;

/**
 * ObjectUtils.java
 * 
 * Static functions to check various state of object (empty, null, etc)
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public class Utils {
    
    public static final String SPACE = " ";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());
    
    public static boolean isEmpty(String pValue) {
        return pValue == null || pValue.isEmpty();
    }
    
    public static <E> boolean equals(E pEntity1, E pEntity2) {
        if (pEntity1 != null) {
            return pEntity1.equals(pEntity2);
        }
        
        return false;
    }
    
    public static boolean areOneEmpty(Object... pValues) {
        
        if (pValues != null) {
            
            for (Object v : pValues) {
                if (v instanceof String && isEmpty((String) v)) {
                    return true;
                } else if (v == null) {
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
    
    public static boolean isAfterNow(LocalDateTime pValidUntil) {
        if (pValidUntil != null) {
            LocalDateTime now = LocalDateTime.now();
            return pValidUntil.isAfter(now);
        }
        
        return true;
    }
    
    public static <E, T> List<T> convert(List<E> pElements, Function<E, T> pFunc) {
        
        List<T> l = new ArrayList<>();
        
        if (pElements != null) {
            for (E e : pElements) {
                l.add(pFunc.apply(e));
            }
        }
        
        return l;
    }
    
    public static <E> String join(List<E> pElements, Function<E, String> pFunc) {
        return join(SPACE, pElements, pFunc);
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
    
    public static <E> List<E> explode(String pElementsStr, Function<String, E> pFunc) {
        return explode(SPACE, pElementsStr, pFunc);
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
    
    public static <O> O coalesce(O... pEntities) {
        if (pEntities != null) {
            for (O e : pEntities) {
                if (e != null) {
                    return e;
                }
            }
        }
        
        return null;
    }
    
    public static <E> boolean contains(E[] pEntities, E pValue) {
        if (pEntities != null) {
            for (E e : pEntities) {
                if (e.equals(pValue)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static <E> boolean contains(List<E> pEntities, E pValue) {
        if (pEntities != null) {
            for (E e : pEntities) {
                if (e.equals(pValue)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static MultiValueMap<String, String> asMap(String... pKeyValues) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();

        if (pKeyValues != null) {
            
            if (pKeyValues.length % 2 != 0) {
                throw new ServerException(new ArrayIndexOutOfBoundsException());                
            }
            
            for (int i = 0, j = pKeyValues.length; i < j; i += 2) {
                p.add(pKeyValues[i], pKeyValues[i+1]);
            }
        }
          
        return p;
    }
    
    /**
     * return a String from the input.
     * If the object is not null, it will be convert to string, else an empty string is returned.
     * @param pObject The object to convert.
     * @return A String.
     */
    public static String asString(Object pObject) {
        if (pObject != null) {
            return pObject.toString();
        }
        return "";
    }
    
    public static <E> E find(List<E> pEntities, Function<E, Boolean> pFunc) {
        if (pEntities != null) {
            
            for (E e : pEntities) {
                if (pFunc.apply(e)) {
                    return e;
                }
            }
        }
        return null;
    }
    

    /**
     * Get a positive id.
     * @return next positive id.
     */
    public static Long nextPositiveId() {
        return counter.incrementAndGet();
    }
    
    /**
     * Get a negative id.
     * @return next negative id.
     */
    public static Long nextNegativeId() {
        return -nextPositiveId();
    }
}
