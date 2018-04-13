/**
 * Copyright 2017 Remi Guillemette - n4dev.ca
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ca.n4dev.aegaeon.server.controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.n4dev.aegaeon.api.exception.OAuthErrorType;
import ca.n4dev.aegaeon.api.exception.OAuthPublicJsonException;
import ca.n4dev.aegaeon.api.exception.OAuthPublicRedirectionException;
import ca.n4dev.aegaeon.api.exception.OauthRestrictedException;
import ca.n4dev.aegaeon.api.exception.OpenIdException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.protocol.ResponseType;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * ControllerErrorInterceptor.java
 *
 * TODO(rguillemette) Add description
 *
 * @author by rguillemette
 * @since May 12, 2017
 */
@ControllerAdvice
public class ControllerErrorInterceptor {


    /*
    * 1- Redirection (auth-endpoint with question mark or fragment)
    *   > error, error_description, error_uri, state
    *
    * 2- Return Json (token endpoint)
    *   > error, error_description, error_uri
    *
    * 3- Nothing returned (displayed by Aegaeon)
    *   > error, error_description, client information
    *
    * */

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerErrorInterceptor.class);

    private static final String ERROR_VIEW = "error";
    private static final String HASHTAG = "#";
    private static final String QUESTIONMARK = "?";


    private OpenIdEventLogger openIdEventLogger;
    private ServerInfo serverInfo;



    public ControllerErrorInterceptor(OpenIdEventLogger pOpenIdEventLogger, ServerInfo pServerInfo) {
        this.openIdEventLogger = pOpenIdEventLogger;
        this.serverInfo = pServerInfo;
    }

    private ModelAndView getBasicPage(String pViewName, Throwable pThrowable) {
        ModelAndView mv = new ModelAndView(pViewName);

        mv.addObject("error", pThrowable);
        mv.addObject("serverInfo", this.serverInfo);

        return mv;
    }

    /**
     *
     * @param pOAuthPublicException
     * @return
     */
    @ExceptionHandler(OAuthPublicRedirectionException.class)
    public RedirectView oauthPublicException(final OAuthPublicRedirectionException pOAuthPublicException) {

        this.openIdEventLogger.log(OpenIdEvent.PUBLIC_ERROR,
                                   (Class<?>) Utils.coalesce(pOAuthPublicException.getSource(), pOAuthPublicException.getClass()),
                                   null,
                                   pOAuthPublicException);

        String url = UriBuilder.build(pOAuthPublicException.getRedirectUrl(), pOAuthPublicException);

        if (Utils.contains(pOAuthPublicException.getAuthRequest().getResponseTypes(), ResponseType.code)) {
            return new RedirectView(url, false);

            // TODO(RG) check the rule here
        } else if (Utils.contains(pOAuthPublicException.getAuthRequest().getResponseTypes(), ResponseType.id_token)) {
            // TODO(RG): this smell bad
            url = url.replace(QUESTIONMARK, HASHTAG);
            return new RedirectView(url, false);
        } else {
            // TODO(RG) Client Cred: check what we should do in this case
            return new RedirectView(url, false);
        }
    }

    /**
     *
     * @param pOauthRestrictedException
     * @return
     */
    @ExceptionHandler(OauthRestrictedException.class)
    public ModelAndView oauthRestrictedException(final OauthRestrictedException pOauthRestrictedException) {
        this.openIdEventLogger.log(OpenIdEvent.RESTRICTED_ERROR, pOauthRestrictedException.getSource(), null, pOauthRestrictedException);

        ModelAndView mv = getBasicPage("error", pOauthRestrictedException);

        mv.addObject("type", "OauthRestrictedException");

        return mv;
    }

    @ExceptionHandler(OAuthPublicJsonException.class)
    @ResponseBody
    public ResponseEntity<OAuthPublicJsonException> oauthPublicJsonException(
            final OAuthPublicJsonException pOAuthPublicJsonException) {

        this.openIdEventLogger.log(OpenIdEvent.PUBLIC_ERROR, pOAuthPublicJsonException.getSource(), null, pOAuthPublicJsonException);

        return new ResponseEntity<OAuthPublicJsonException>(pOAuthPublicJsonException,
                                                            getStatusFromError(pOAuthPublicJsonException.getError()));
    }


    @ExceptionHandler(OpenIdException.class)
    public Object openIdException(OpenIdException pOpenIdException,
                                  HttpServletRequest pHttpServletRequest,
                                  HttpServletResponse pHttpServletResponse) {
        this.openIdEventLogger.log(OpenIdEvent.OTHERS, pOpenIdException.getSource(), pOpenIdException.getError());

        /* logic here */

        ModelAndView mv = getBasicPage("error", pOpenIdException);

        mv.addObject("type", "OauthRestrictedException");

        return mv;
    }

    private ModelAndView toHtmlView(Map<String, ? extends Object> pModel) {
        ModelAndView view = new ModelAndView(ERROR_VIEW);
        view.addAllObjects(pModel);
        return view;
    }

    private ModelAndView toJsonView(Map<String, Object> pModel, HttpServletResponse pHttpServletResponse) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        try {
            jsonConverter.write(pModel, jsonMimeType, new ServletServerHttpResponse(pHttpServletResponse));
            return null;
        } catch (IOException pIOException) {
            return toHtmlView(Utils.asMap("exception", pIOException.getMessage()));
        }

    }

    private HttpStatus getStatusFromError(OAuthErrorType pOAuthErrorType) {
        if (pOAuthErrorType != null) {
            switch (pOAuthErrorType) {
                case temporarily_unavailable:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case invalid_grant:
                case invalid_scope:
                case unsupported_response_type:
                    return HttpStatus.BAD_REQUEST;

                case unauthorized_client:
                    return HttpStatus.UNAUTHORIZED;

            }
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView notFoundException(final NoHandlerFoundException pNoHandlerFoundException) {
        LOGGER.error("Not found");

        ModelAndView mv = getBasicPage("error-not-found", pNoHandlerFoundException);
        mv.addObject("requestUrl", pNoHandlerFoundException.getRequestURL());
        mv.addObject("serverInfo", this.serverInfo);

        return mv;
    }

    @ExceptionHandler(ServerException.class)
    public ModelAndView genericServerException(final ServerException pServerException) {
        LOGGER.error("Generic ServerException", pServerException);

        ModelAndView mv = getBasicPage("error", pServerException);
        String errorMessage = (pServerException != null ? pServerException.getMessage() : "Unknown error");

        mv.addObject("type", "ServerException");
        mv.addObject("errorMessage", errorMessage);
        mv.addObject("errorCode", pServerException.getCode().toString());

        return mv;
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView exception(final Throwable pThrowable) {
        LOGGER.error("Generic Exception", pThrowable);

        ModelAndView mv = getBasicPage("error", pThrowable);
        String errorMessage = (pThrowable != null ? pThrowable.getMessage() : "Unknown error");

        mv.addObject("type", "Throwable");
        mv.addObject("errorMessage", errorMessage);

        return mv;
    }

}
