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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.n4dev.aegaeon.api.model.User;
import ca.n4dev.aegaeon.api.repository.UserRepository;
import ca.n4dev.aegaeon.server.security.AegaeonUserDetails;

/**
 * UserDetailsService.java
 * 
 * A simple service to load a userdetails.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
@Service("userDetailsService")
public class SpringAuthUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    
    /**
     * Default Constructor.
     * @param pUserRepository Repo to access users.
     */
    @Autowired
    public SpringAuthUserDetailsService(UserRepository pUserRepository) {
        this.userRepository = pUserRepository;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String pUsername) throws UsernameNotFoundException {
        User user = this.userRepository.findByUserName(pUsername);
        
        if (user != null) {
            List<SimpleGrantedAuthority> auths = new ArrayList<>();
            user.getAuthorities().forEach(a -> auths.add(new SimpleGrantedAuthority(a.getCode() /*a.getCode().replace("ROLE_", "")*/)));
            return new AegaeonUserDetails(user.getId(), user.getUserName(), user.getPasswd(), user.isEnabled(), true, auths);
        }
        
        throw new UsernameNotFoundException(pUsername + " not found");
    }
    
}
