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
package ca.n4dev.aegaeon.server.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * PageListDto.java
 * 
 * A simple dto to hold a list of elements.
 * 
 * @author by rguillemette
 * @since Oct 25, 2017
 */
public class PageListDto<E, T> {
    
    protected List<T> elements = new ArrayList<>();
    
    public PageListDto(Page<E> pPage, Function<E, T> pConverter) {
        if (pPage != null && pPage.getContent() != null) {
            pPage.getContent().forEach(e -> {
                this.elements.add(pConverter.apply(e));
            });
        }
    }

    public PageListDto(List<T> pElements) {
        this.elements = pElements;
    }

    /**
     * @return the elements
     */
    public List<T> getElements() {
        return elements;
    }
}
