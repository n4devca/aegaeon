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
package ca.n4dev.aegaeon.server.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ClientRequestedUri.java
 * 
 * Uris that can be fetched (and cached) by the OP to be used by a client during authorization.
 *
 * @author by rguillemette
 * @since Jun 20, 2017
 */
@Entity
@Table(name = "client_request_uris")
public class ClientRequestedUri extends BaseEntity {

    private String uri;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param pUri the uri to set
     */
    public void setUri(String pUri) {
        uri = pUri;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param pClient the client to set
     */
    public void setClient(Client pClient) {
        client = pClient;
    }
}
