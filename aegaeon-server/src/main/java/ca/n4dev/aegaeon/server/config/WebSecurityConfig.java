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
package ca.n4dev.aegaeon.server.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * WebSecurityConfig.java
 * 
 * Spring Security configuration.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    @Configuration
    @Order(1)
    public static class ClientAuthWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        
        @Autowired
        private UserDetailsService clientDetailsService;
        
        @Autowired
        private AuthenticationEntryPoint authenticationEntryPoint;
        
        @Override
        protected void configure(HttpSecurity pHttp) throws Exception {
            pHttp
                .antMatcher("/token")
                    .authorizeRequests()
                    .anyRequest().hasAnyAuthority("CLIENT")
                .and()
                .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf().disable()
                .userDetailsService(clientDetailsService);
        }
        
    }
    
    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        
        @Autowired
        private UserDetailsService userDetailsService;
        
        @Autowired
        private PasswordEncoder passwordEncoder;
        
        @Override
        protected void configure(HttpSecurity pHttp) throws Exception {
            pHttp
                .authorizeRequests()
                    .antMatchers("/resources/**").permitAll()
                    .antMatchers("/", "/home").permitAll()
                    //.anyRequest().authenticated()
                    .antMatchers("/authorize").hasAnyAuthority("USER")
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .userDetailsService(userDetailsService)
                .logout()
                    .logoutSuccessUrl("/");
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder pAuth) throws Exception {
            
            pAuth.userDetailsService(userDetailsService)
                 .passwordEncoder(passwordEncoder);
            
        }
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            
            @Override
            public void commence(HttpServletRequest pRequest, HttpServletResponse pResponse, AuthenticationException pException)
                    throws IOException, ServletException {
                pResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }
    
//    public static void main(String[] args) {
//        PasswordEncoder p = new BCryptPasswordEncoder();
//        System.out.println(p.encode("admin@localhost"));
//    }
}
