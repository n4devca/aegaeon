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

import java.security.SecureRandom;

import ca.n4dev.aegaeon.api.utils.Randomizer;

/**
 * StringRandomizer.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 9, 2017
 */
public class StringRandomizer implements Randomizer {
    
    public static final char[] ALPHA_NUM = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'N', 'M', 
                                            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'X', 'Y', 'Z', 
                                            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'n', 'm', 
                                            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z', 
                                            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static volatile Randomizer singletonInstance;

    public static Randomizer getInstance() {
        if ( singletonInstance == null ) {
            synchronized ( StringRandomizer.class ) {
                if ( singletonInstance == null ) {
                    singletonInstance = new StringRandomizer();
                }
            }
        }
        return singletonInstance;
    }
    
    private SecureRandom secureRandom = null;
    
    private StringRandomizer() {
        this.secureRandom = new SecureRandom();
    }
    
    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.utils.Randomizer#getRandomString(int, char[])
     */
    @Override
    public String getRandomString(int pLength, char[] pCharacterSet) {
        StringBuilder sb = new StringBuilder();
        
        for (int loop = 0; loop < pLength; loop++) {
            int index = secureRandom.nextInt(pCharacterSet.length);
            sb.append(pCharacterSet[index]);
        }
        
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.utils.Randomizer#getRandomString(int)
     */
    @Override
    public String getRandomString(int pLength) {
        return getRandomString(pLength, ALPHA_NUM);
    }

    
}
