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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import ca.n4dev.aegaeon.api.utils.Randomizer;

/**
 * RandomizerTests.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since Jun 9, 2017
 */
public class RandomizerTests {

    @Test
    public void testRandomString() {
        Randomizer random = StringRandomizer.getInstance();
        String v1 = random.getRandomString(20);
        
        Assert.assertNotNull(v1);
        Assert.assertTrue(v1.length() == 20);
    }
    
    @Test
    public void testManyRandomString() {
        Randomizer random = StringRandomizer.getInstance();
        Map<String, Integer> counter = new LinkedHashMap<>(); 
        
        for (int i = 0; i < 10000; i++) {
            String key = random.getRandomString(20);
            Integer c = counter.get(key);
            
            if (c == null) {
                counter.put(key, 1);
            } else {
                counter.put(key, c++);
            }
        }
        
        for (Entry<String, Integer> en : counter.entrySet()) {
            System.out.println(en.getKey());
            Assert.assertTrue("" + en.getKey() + " => " + en.getValue(), en.getValue() == 1);
        }
    }
}
