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

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import ca.n4dev.aegaeon.server.controller.interceptor.RequestMethodArgumentResolver;
import ca.n4dev.aegaeon.server.controller.interceptor.ServerInfoInterceptor;

/**
 * WebMvcConfig.java
 * 
 * Spring mvc configuration.
 *
 * @author by rguillemette
 * @since May 11, 2017
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    
    @Value("${aegaeon.info.issuer}")
    private String issuer;
    
    @Value("${aegaeon.info.serverName:Aegaeon Server}")
    private String serverName;
    
    @Value("${aegaeon.info.logoUrl:#{null}}")
    private String logoUrl;
    
    @Value("${aegaeon.info.legalEntity:#{null}}")
    private String legalEntity;

    @Value("${aegaeon.info.privacyPolicy:#{null}}")
    private String privacyPolicy;
    
    @Value("${aegaeon.info.customStyleSheet:#{null}}")
    private String customStyleSheet;
    
    
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.CANADA);
        
        return resolver;
    }
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }
    
    @Bean
    public ServerInfo serverInfo() {
        return new ServerInfo(this.issuer, 
        					  this.serverName,
        					  this.legalEntity, 
        					  this.logoUrl, 
        					  this.privacyPolicy, 
        					  this.customStyleSheet);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(new ServerInfoInterceptor(serverInfo()));
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addArgumentResolvers(java.util.List)
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> pArgumentResolvers) {
        pArgumentResolvers.add(new RequestMethodArgumentResolver());
    }
}
