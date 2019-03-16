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
package ca.n4dev.aegaeon.server.service;

import java.util.ArrayList;
import java.util.List;

import ca.n4dev.aegaeon.api.model.Client;
import ca.n4dev.aegaeon.api.repository.ClientRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * SpringAuthClientDetailsService.java
 * 
 * A service implementing spring's userdetailsservice
 * and used to authenticate clients.
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
@Service("clientDetailsService")
public class SpringAuthClientDetailsService implements UserDetailsService {

    private ClientRepository clientRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SpringAuthClientDetailsService(ClientRepository pClientRepository, PasswordEncoder pPasswordEncoder) {
        this.clientRepository = pClientRepository;
        this.passwordEncoder = pPasswordEncoder;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String pUsername) throws UsernameNotFoundException {
        Client client = this.clientRepository.findByPublicId(pUsername);
        
        if (client != null) {
            List<SimpleGrantedAuthority> auths = new ArrayList<>();
            auths.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
            return new AegaeonUserDetails(client.getId(), pUsername, "{noop}" + client.getSecret(), true, true, auths);
        }
        
        throw new UsernameNotFoundException(pUsername + " not found");
    }

}
