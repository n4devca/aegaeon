/*
 * *
 *  * Copyright 2017 Remi Guillemette - n4dev.ca
 *  *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 *
 */

package ca.n4dev.aegaeon.server.utils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Differentiation.java.
 *
 * The result of differentiation of two list with new objects, updated objects and object to delete.
 *
 * @see Utils#differentiate(List, List, BiFunction, BiFunction, Function);
 */
public class Differentiation<E> {

    private List<E> newObjs;
    private List<E> updatedObjs;
    private List<E> removedObjs;

    public Differentiation(List<E> pNewObjs, List<E> pUpdatedObjs, List<E> pRemovedObjs) {
        this.newObjs = pNewObjs;
        this.updatedObjs = pUpdatedObjs;
        this.removedObjs = pRemovedObjs;
    }

    /**
     * @return the objects to remove.
     */
    public List<E> getRemovedObjs() {
        return removedObjs;
    }

    /**
     * @return the objects to update.
     */
    public List<E> getUpdatedObjs() {
        return updatedObjs;
    }

    /**
     * @return the objects to create.
     */
    public List<E> getNewObjs() {
        return newObjs;
    }
}
