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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PageDto.java
 * 
 * A dto reprensenting loaded elements and a pagination.
 *
 * @author by rguillemette
 * @since Dec 6, 2017
 */
public class PageDto<D> {

    private List<D> elements;
    private int pageNumber;
    private long totalPage;
    private int pageSize;
    
    public PageDto(List<D> pElements, Pageable pPageable, Long pTotalElements) {
        this.elements = pElements;
        this.pageNumber = pPageable.getPageNumber();
        this.pageSize = pPageable.getPageSize();
        this.totalPage = pTotalElements;
    }

    /**
     * @return the elements
     */
    public List<D> getElements() {
        return elements;
    }

    /**
     * @param pElements the elements to set
     */
    public void setElements(List<D> pElements) {
        elements = pElements;
    }

    /**
     * @return the pageNumber
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * @param pPageNumber the pageNumber to set
     */
    public void setPageNumber(int pPageNumber) {
        pageNumber = pPageNumber;
    }

    /**
     * @return the totalPage
     */
    public long getTotalPage() {
        return totalPage;
    }

    /**
     * @param pTotalPage the totalPage to set
     */
    public void setTotalPage(long pTotalPage) {
        totalPage = pTotalPage;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pPageSize the pageSize to set
     */
    public void setPageSize(int pPageSize) {
        pageSize = pPageSize;
    }
}
