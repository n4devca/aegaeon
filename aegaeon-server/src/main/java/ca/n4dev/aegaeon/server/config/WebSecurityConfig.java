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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import ca.n4dev.aegaeon.server.controller.ControllerErrorInterceptor;
import ca.n4dev.aegaeon.server.controller.IntrospectController;
import ca.n4dev.aegaeon.server.controller.PublicJwkController;
import ca.n4dev.aegaeon.server.controller.ServerInfoController;
import ca.n4dev.aegaeon.server.controller.SimpleCreateAccountController;
import ca.n4dev.aegaeon.server.controller.SimpleHomeController;
import ca.n4dev.aegaeon.server.controller.SimpleUserAccountController;
import ca.n4dev.aegaeon.server.controller.TokensController;
import ca.n4dev.aegaeon.server.controller.UserInfoController;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthenticationFilter;
import ca.n4dev.aegaeon.server.security.AccessTokenAuthenticationProvider;
import ca.n4dev.aegaeon.server.security.PromptAwareAuthenticationFilter;
import ca.n4dev.aegaeon.server.service.AuthenticationService;
import ca.n4dev.aegaeon.server.service.UserAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * WebSecurityConfig.java
 * 
 * Spring Security configuration.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private static final String BCRYPT_PASSWD_ENCODER_PREFIX = "bcrypt";
    private static final String NOOP_PASSWD_ENCODER_PREFIX = "noop";
    private static final String ROLE_CLIENT = "ROLE_CLIENT";
    private static final String ROLE_USER = "ROLE_USER";

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder bcryptPasswordEncoder =  new BCryptPasswordEncoder();

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(BCRYPT_PASSWD_ENCODER_PREFIX, bcryptPasswordEncoder);
        encoders.put(NOOP_PASSWD_ENCODER_PREFIX, NoOpPasswordEncoder.getInstance());

        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(BCRYPT_PASSWD_ENCODER_PREFIX, encoders);
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder);

        return delegatingPasswordEncoder;
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
                    .requestMatchers()
                    .antMatchers(TokensController.URL, IntrospectController.URL)
                    .and()
                    .authorizeRequests().anyRequest().hasAnyAuthority(ROLE_CLIENT)
                    .and()
                    .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable()
                    .userDetailsService(clientDetailsService);
        }

//        @Autowired
//        public void configureGlobal(AuthenticationManagerBuilder pAuth) throws Exception {
//            pAuth.userDetailsService(clientDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
//
//        }
        
    }

    @Configuration
    @Order(2)
    public static class UserInfoWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        
        @Autowired
        private AuthenticationEntryPoint authenticationEntryPoint;
        
        @Autowired
        private ServerInfo serverInfo;
        
        @Autowired
        private AuthenticationService authenticationService;
        
        /**
         * Remember me config
         */
        @Override 
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(accessTokenAuthenticationProvider());
        }
        
        
        public AccessTokenAuthenticationFilter accessTokenAuthenticationFilter() throws Exception{
            return new AccessTokenAuthenticationFilter(authenticationManagerBean(), authenticationEntryPoint);
        }
        
        
        public AccessTokenAuthenticationProvider accessTokenAuthenticationProvider() {
            return new AccessTokenAuthenticationProvider(authenticationService, serverInfo);
        }
        
        @Bean 
        @Override 
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
        
        @Override
        protected void configure(HttpSecurity pHttp) throws Exception {
            pHttp
                    .antMatcher(UserInfoController.URL)
                    .authorizeRequests()
                    .anyRequest().hasAnyAuthority(ROLE_USER)
                    .and()
                    .csrf().disable()
                    .addFilterBefore(accessTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    ;
        }
    }
    
    
    @Configuration
    // @Order(3)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        
        @Autowired
        private UserDetailsService userDetailsService;
        
        @Autowired
        private ControllerErrorInterceptor controllerErrorInterceptor;

        @Autowired
        private UserAuthorizationService userAuthorizationService;
        
        @Autowired
        private PasswordEncoder passwordEncoder;

        
        public PromptAwareAuthenticationFilter promptAwareAuthenticationFilter() {
            return new PromptAwareAuthenticationFilter(this.userAuthorizationService, this.controllerErrorInterceptor);
        }
        
        @Override
        protected void configure(HttpSecurity pHttp) throws Exception {
            pHttp
                    .authorizeRequests()
                    // public
                    .antMatchers("/resources/**",
                                 ServerInfoController.URL,
                                 PublicJwkController.URL,
                                 SimpleHomeController.URL,
                                 SimpleCreateAccountController.URL,
                                 SimpleCreateAccountController.URL_ACCEPT).permitAll()
                    .anyRequest()
                    .hasAnyAuthority(ROLE_USER)
                    .and()
                    .addFilterBefore(promptAwareAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl(SimpleUserAccountController.URL)
                    .and()
                    .csrf().disable()
                    .userDetailsService(userDetailsService)
                    .logout()
                    .logoutSuccessUrl("/");
        }

//        @Autowired
//        public void configureGlobal(AuthenticationManagerBuilder pAuth) throws Exception {
//            pAuth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//
//        }
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (pRequest, pResponse, pException) -> pResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
