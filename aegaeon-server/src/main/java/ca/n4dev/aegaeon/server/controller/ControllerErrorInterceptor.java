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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.n4dev.aegaeon.api.exception.ErrorHandling;
import ca.n4dev.aegaeon.api.exception.OpenIdErrorType;
import ca.n4dev.aegaeon.api.exception.OpenIdException;
import ca.n4dev.aegaeon.api.exception.ServerException;
import ca.n4dev.aegaeon.api.exception.ServerExceptionCode;
import ca.n4dev.aegaeon.api.exception.Severity;
import ca.n4dev.aegaeon.api.logging.OpenIdEvent;
import ca.n4dev.aegaeon.api.logging.OpenIdEventLogger;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * ControllerErrorInterceptor.java
 * <p>
 * Controller catching all exception and able to answer to UA or display
 * an internal error page.
 *
 * @author by rguillemette
 * @since May 12, 2017
 */
@ControllerAdvice
public class ControllerErrorInterceptor extends BaseUiController {


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
    private static final String REDIRECTION_ERROR_KEY = "error";
    private static final String REDIRECTION_DESC_KEY = "error_description";
    private static final String REDIRECTION_STATE_KEY = "state";

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private OpenIdEventLogger openIdEventLogger;
    private ServerInfo serverInfo;

    /**
     * Constructor.
     *
     * @param pOpenIdEventLogger event Logger.
     * @param pServerInfo        Aegaeon server info.
     */
    @Autowired
    public ControllerErrorInterceptor(OpenIdEventLogger pOpenIdEventLogger, ServerInfo pServerInfo, MessageSource pMessageSource) {
        super(pMessageSource);
        this.openIdEventLogger = pOpenIdEventLogger;
        this.serverInfo = pServerInfo;
    }


    @ExceptionHandler(OpenIdException.class)
    public Object openIdException(OpenIdException pOpenIdException,
                                  Locale pLocale,
                                  HttpServletRequest pHttpServletRequest,
                                  HttpServletResponse pHttpServletResponse) {
        // TODO: Log by type
        this.openIdEventLogger.log(OpenIdEvent.OTHERS, pOpenIdException.getSource(), pOpenIdException.getError());
        ErrorHandling answerType = getErrorAnswerType(pOpenIdException);

        if (answerType == ErrorHandling.JSON) {
            return toJsonView(pOpenIdException, pHttpServletResponse);
        } else if (answerType == ErrorHandling.REDIRECT) {
            return redirectErrorToClient(pOpenIdException.getClientUri(), pOpenIdException);
        }

        // Default to internal
        return internalErrorPage(Severity.INFO,
                                 pLocale,
                                 OpenIdErrorType.fromServerCode(pOpenIdException.getCode()).toString(),
                                 null,
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView notFoundException(final NoHandlerFoundException pNoHandlerFoundException,
                                          Locale pLocale,
                                          HttpServletRequest pHttpServletRequest,
                                          HttpServletResponse pHttpServletResponse) {

        //return internalErrorPage(pNoHandlerFoundException, pHttpServletRequest, pHttpServletResponse);

        return internalErrorPage(Severity.INFO,
                                 pLocale,
                                 "notfound",
                                 getLabel("page.error.list.notfound", new String[]{pNoHandlerFoundException.getRequestURL()}, pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);

    }

    @ExceptionHandler(ServerException.class)
    public ModelAndView genericServerException(final ServerException pServerException,
                                               Locale pLocale,
                                               HttpServletRequest pHttpServletRequest,
                                               HttpServletResponse pHttpServletResponse) {

        return internalErrorPage(Severity.WARNING,
                                 pLocale,
                                 "serverexception",
                                 getLabel("serverexception", new String[]{pServerException.getCode().toString()}, pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView exception(final Throwable pThrowable,
                                  Locale pLocale,
                                  HttpServletRequest pHttpServletRequest,
                                  HttpServletResponse pHttpServletResponse) {

        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 null,
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }


    private ModelAndView internalErrorPage(Severity pSeverity,
                                           Locale pLocale,
                                           String pErrorType,
                                           String pErrorMessage,
                                           HttpServletRequest pHttpServletRequest,
                                           HttpServletResponse pHttpServletResponse) {

        LOGGER.error("internalErrorPage",
                     new StringBuilder()
                             .append(pSeverity)
                             .append(">")
                             .append(pErrorType)
                             .append("/")
                             .append(pErrorMessage)
                             .toString());

        /*
         * date
         * severity
         * errorType
         * errorMessage
         * descriptionCode | description
         * */

        ModelAndView mv = new ModelAndView(ERROR_VIEW);

        String errorMessage = Utils.isNotEmpty(pErrorMessage) ?
                pErrorMessage : getLabel("page.error.list." + pErrorType, pLocale);

        mv.addObject("date", formatter.format(LocalDateTime.now()));
        mv.addObject("severity", pSeverity.toString());
        mv.addObject("errorMessage", errorMessage);
        mv.addObject("errorType", pErrorType);
        mv.addObject("serverInfo", this.serverInfo);


        return mv;
    }

    private RedirectView redirectErrorToClient(String pRedirectionUri, OpenIdException pOpenIdException) {
        RedirectView view = new RedirectView();
        view.setContextRelative(false);

        // Build Url
        String url = UriBuilder.build(pRedirectionUri,
                                      pOpenIdException,
                                      pOpenIdException.getRequestedGrantType() == GrantType.IMPLICIT);

        view.setUrl(url);

        return view;
    }


    private ModelAndView toHtmlView(Map<String, ?> pModel) {
        ModelAndView view = new ModelAndView(ERROR_VIEW);
        view.addAllObjects(pModel);
        return view;
    }

    private ModelAndView toJsonView(OpenIdException pOpenIdException, HttpServletResponse pHttpServletResponse) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        try {

            Map<String, String> models = UriBuilder.buildModel(pOpenIdException);
            jsonConverter.write(models, jsonMimeType, new ServletServerHttpResponse(pHttpServletResponse));
            return null;

        } catch (IOException pIOException) {
            return toHtmlView(Utils.asMap("exception", pIOException.getMessage()));
        }

    }

    private boolean isCode(ServerExceptionCode pServerExceptionCode, ServerExceptionCode pCode) {
        return Utils.equals(pServerExceptionCode, pCode);
    }

    private ErrorHandling getErrorAnswerType(OpenIdException pOpenIdException) {

        if (pOpenIdException != null) {

            if (isCode(ServerExceptionCode.INTROSPECT_PARAM_INVALID, pOpenIdException.getCode())) {
                return ErrorHandling.JSON;
            } else if (isCode(ServerExceptionCode.CLIENT_REDIRECTIONURL_INVALID, pOpenIdException.getCode()) ||
                    isCode(ServerExceptionCode.CLIENT_EMPTY, pOpenIdException.getCode()) ||
                    isCode(ServerExceptionCode.AUTH_CODE_UNEXPECTED_CLIENT, pOpenIdException.getCode()) ||
                    isCode(ServerExceptionCode.AUTH_CODE_UNEXPECTED_REDIRECTION, pOpenIdException.getCode()) ||
                    Utils.isEmpty(pOpenIdException.getClientUri())) {

                return ErrorHandling.INTERNAL;

            } else if (Utils.isNotEmpty(pOpenIdException.getClientUri()) &&
                    Utils.isNotEmpty(pOpenIdException.getClientPublicId())) {

                if (pOpenIdException.getErrorHandling() == ErrorHandling.REDIRECT) {
                    return ErrorHandling.REDIRECT;
                } else {
                    return ErrorHandling.JSON;
                }
            }
        }

        // Stricter in case we missed something
        return ErrorHandling.INTERNAL;
    }

}
