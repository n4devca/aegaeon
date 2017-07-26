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
package ca.n4dev.aegaeon.api.caching;

/**
 * CacheProvider.java
 * 
 * A simple interface describing a cache provider used by aegaeon.
 * To add a new Cache Provider, implement this and configure the server 
 * to use your provider.
 *
 * @param <E> Cached entity.
 * 
 * @author by rguillemette
 * @since Jul 21, 2017
 */
public interface CacheProvider {

    String get(String pNameSpace, String pKey);
    
    void set(String pNameSpace, String pKey, int pTTL, String pEntity);
    
    void remove(String pNameSpace, String pKey);
    
    void clear(String pNameSpace);
    
}
