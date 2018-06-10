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
package ca.n4dev.aegaeon.api.utils;

import java.util.ArrayList;

/**
 * LazyList.java
 * 
 * A list able to auto extend when add and set is used with an index bigger than size().
 * 
 * @author by rguillemette
 * @since Sep 28, 2017
 */
public class LazyList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 9091734767506972577L;

    /* (non-Javadoc)
     * @see java.util.ArrayList#set(int, java.lang.Object)
     */
    @Override
    public E set(int pIndex, E pElement) {
        extendTo(pIndex);
        return super.set(pIndex, pElement);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#add(int, java.lang.Object)
     */
    @Override
    public void add(int pIndex, E pElement) {
        extendTo(pIndex);
        super.add(pIndex, pElement);
    }


    private void extendTo(int pIndex) {
        if (pIndex > size()) {
            for (int i = size() - 1; i < pIndex; i ++) {
                add(null);
            }
        }
    }
}
