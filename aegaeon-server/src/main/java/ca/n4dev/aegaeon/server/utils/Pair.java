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

/**
 * Pair.java
 * 
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
public class Pair<L, R> {

    private L left;
    
    private R right;
    
    public Pair(L pLeft, R pRight) {
        this.left = pLeft;
        this.right = pRight;
    }

    /**
     * @return the left
     */
    public L getLeft() {
        return left;
    }

    /**
     * @param pLeft the left to set
     */
    public void setLeft(L pLeft) {
        left = pLeft;
    }

    /**
     * @return the right
     */
    public R getRight() {
        return right;
    }

    /**
     * @param pRight the right to set
     */
    public void setRight(R pRight) {
        right = pRight;
    }
    
    
}
