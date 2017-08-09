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
package ca.n4dev.aegaeon.api.protocol;

/**
 * Display.java
 * 
 * Enum describing display parameter.
 *
 * @author by rguillemette
 * @since Aug 7, 2017
 */
public enum Display {
    page, popup, touch, wap;
    
    public static final Display from(String pDisplayValue) {
        
        for (Display d : values()) {
            if (d.toString().equalsIgnoreCase(pDisplayValue)) {
                return d;
            }
        }
        
        return null;
    }
}
