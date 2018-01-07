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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ClientContact.java
 * 
 * Client's contacts.
 *
 * @author by rguillemette
 * @since Jun 20, 2017
 */
@Entity
@Table(name = "client_contact")
public class ClientContact extends BaseEntity {

    private String email;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    /**
     * Default Constructor.
     */
    public ClientContact() {}
    
    /**
     * Build a complete ClientContact.
     * @param pClient The client.
     * @param pEmail The email.
     */
    public ClientContact(Client pClient, String pEmail) {
        this.client = pClient;
        this.email = pEmail;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param pEmail the email to set
     */
    public void setEmail(String pEmail) {
        email = pEmail;
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
