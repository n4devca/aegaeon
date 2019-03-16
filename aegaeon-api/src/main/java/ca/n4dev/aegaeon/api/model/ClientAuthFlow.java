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
package ca.n4dev.aegaeon.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ca.n4dev.aegaeon.api.protocol.Flow;

/**
 * ClientGrantType.java
 * 
 * A grant (selected or not) by a client.
 *
 * @author by rguillemette
 * @since Dec 9, 2017
 */

@Entity
@Table(name = "client_auth_flow")
public class ClientAuthFlow extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "flow")
    @Enumerated(EnumType.STRING)
    private Flow flow;

    /**
     * Default Constructor.
     */
    public ClientAuthFlow() {}

    /**
     * Full Constructor.
     * @param pClient The associated client.
     */
    public ClientAuthFlow(Client pClient, Flow pFlow) {
        this.client = pClient;
        this.flow = pFlow;
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

    /**
     * @return the flow
     */
    public Flow getFlow() {
        return flow;
    }

    /**
     * @param pFlow the flow to set
     */
    public void setFlow(Flow pFlow) {
        flow = pFlow;
    }
}
