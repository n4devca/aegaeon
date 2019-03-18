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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
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
import ca.n4dev.aegaeon.api.protocol.AuthRequest;
import ca.n4dev.aegaeon.api.protocol.ClientRequest;
import ca.n4dev.aegaeon.api.protocol.FlowUtils;
import ca.n4dev.aegaeon.api.protocol.GrantType;
import ca.n4dev.aegaeon.api.protocol.TokenRequest;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.controller.exception.*;
import ca.n4dev.aegaeon.server.utils.UriBuilder;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    private static MediaType jsonMimeType = MediaType.APPLICATION_JSON;
    private static MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

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


    // ---

    // Internal
    // TODO(RG): proper error display
    @ExceptionHandler(InvalidClientIdException.class)
    public ModelAndView invalidClientIdException(InvalidClientIdException pInvalidClientIdException,
                                                 Locale pLocale,
                                                 HttpServletRequest pHttpServletRequest,
                                                 HttpServletResponse pHttpServletResponse) {

        final String clientId = pInvalidClientIdException.getClientRequest().getClientId();

        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 getLabel("page.error.list.invalidclientid",
                                          new Object[]{clientId}, pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    // TODO(RG): proper error display
    @ExceptionHandler(InvalidClientRedirectionException.class)
    public ModelAndView invalidClientRedirectionException(InvalidClientRedirectionException pInvalidClientRedirectionException,
                                                          Locale pLocale,
                                                          HttpServletRequest pHttpServletRequest,
                                                          HttpServletResponse pHttpServletResponse) {

        final String clientRedirectionUri = pInvalidClientRedirectionException.getClientRequest().getRedirectUri();

        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 getLabel("page.error.list.invalidclientredirection",
                                          new Object[]{clientRedirectionUri}, pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    // TODO(RG): proper error display
    @ExceptionHandler(InternalAuthorizationException.class)
    public ModelAndView internalAuthorizationException(InternalAuthorizationException pInternalAuthorizationException,
                                                       Locale pLocale,
                                                       HttpServletRequest pHttpServletRequest,
                                                       HttpServletResponse pHttpServletResponse) {

        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 getLabel("page.error.list.serverexception",
                                          new Object[]{pInternalAuthorizationException.getCause().getMessage()},
                                          pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    // TODO(RG): proper error display
    @ExceptionHandler(MissingUserInformationException.class)
    public ModelAndView missingUserInformationException(MissingUserInformationException pMissingUserInformationException,
                                                        Locale pLocale,
                                                        HttpServletRequest pHttpServletRequest,
                                                        HttpServletResponse pHttpServletResponse) {
        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 getLabel("page.error.list.serverexception",
                                          new Object[]{"Missing user information"},
                                          pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    // TODO(RG): proper error display
    @ExceptionHandler(UnauthorizedClient.class)
    public ModelAndView unauthorizedClient(UnauthorizedClient pUnauthorizedClient,
                                           Locale pLocale,
                                           HttpServletRequest pHttpServletRequest,
                                           HttpServletResponse pHttpServletResponse) {

        final String clientId = pUnauthorizedClient.getClientRequest().getClientId();

        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 getLabel("page.error.list.unauthorizedclient",
                                          new Object[]{clientId},
                                          pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    // TODO(RG): proper error display
    @ExceptionHandler(UnauthorizedScope.class)
    public ModelAndView unauthorizedScope(UnauthorizedScope pUnauthorizedScope,
                                          Locale pLocale,
                                          HttpServletRequest pHttpServletRequest,
                                          HttpServletResponse pHttpServletResponse) {

        final String scopes = Utils.safeSet(pUnauthorizedScope.getUnauthorizedScopes())
                                   .stream().collect(Collectors.joining(", "));


        return internalErrorPage(Severity.DANGER,
                                 pLocale,
                                 "unexpected",
                                 getLabel("page.error.list.unauthorizedscope",
                                          new Object[]{scopes},
                                          pLocale),
                                 pHttpServletRequest,
                                 pHttpServletResponse);
    }

    // External

    @ExceptionHandler(InvalidFlowException.class)
    public ModelAndView invalidFlowException(InvalidFlowException pInvalidFlowException,
                                             Locale pLocale,
                                             HttpServletRequest pHttpServletRequest,
                                             HttpServletResponse pHttpServletResponse) {

        final AuthRequest authRequest = pInvalidFlowException.getAuthRequest();
        RedirectView view = getRedirectError(authRequest, OpenIdErrorType.unsupported_response_type);
        return new ModelAndView(view);
    }

    @ExceptionHandler(UnauthorizedGrant.class)
    public ModelAndView unauthorizedGrant(UnauthorizedGrant pUnauthorizedGrant,
                                          Locale pLocale,
                                          HttpServletRequest pHttpServletRequest,
                                          HttpServletResponse pHttpServletResponse) {

        final ClientRequest clientRequest = pUnauthorizedGrant.getClientRequest();
        return toJsonView(OpenIdErrorType.invalid_request, pHttpServletResponse);
    }

    @ExceptionHandler(InvalidRequestMethodException.class)
    public ModelAndView invalidRequestMethodException(InvalidRequestMethodException pInvalidRequestMethodException,
                                                      Locale pLocale,
                                                      HttpServletRequest pHttpServletRequest,
                                                      HttpServletResponse pHttpServletResponse) {

        final ClientRequest clientRequest = pInvalidRequestMethodException.getClientRequest();

        // Can be throw from Authorize endpoint or token endpoint
        if (clientRequest instanceof TokenRequest) {
            TokenRequest tokenRequest = (TokenRequest) clientRequest;
            return toJsonView(OpenIdErrorType.invalid_request, pHttpServletResponse);

        } else {
            // From Authorize
            AuthRequest authRequest = (AuthRequest) clientRequest;
            RedirectView view = getRedirectError(authRequest, OpenIdErrorType.invalid_request);
            return new ModelAndView(view);
        }
    }

    @ExceptionHandler(InvalidScopeException.class)
    public ModelAndView invalidScopeException(InvalidScopeException pInvalidScopeException,
                                              Locale pLocale,
                                              HttpServletRequest pHttpServletRequest,
                                              HttpServletResponse pHttpServletResponse) {

        final ClientRequest clientRequest = pInvalidScopeException.getClientRequest();

        // Throw by the service, should not happend but make sure
        if (clientRequest == null) {
            // Hum ?
            return toHtmlView(Utils.asMap("exception", "Invalid scope: " + pInvalidScopeException.getScope()));
        }

        // Can be throw from Authorize endpoint or token endpoint


        if (clientRequest instanceof TokenRequest) {
            TokenRequest tokenRequest = (TokenRequest) clientRequest;
            return toJsonView(OpenIdErrorType.invalid_request, pHttpServletResponse);

        } else {
            // From Authorize
            AuthRequest authRequest = (AuthRequest) clientRequest;
            RedirectView view = getRedirectError(authRequest, OpenIdErrorType.invalid_scope);
            return new ModelAndView(view);
        }

    }

    @ExceptionHandler(InvalidGrantTypeException.class)
    public ModelAndView invalidGrantTypeException(InvalidGrantTypeException pInvalidGrantTypeException,
                                                  Locale pLocale,
                                                  HttpServletRequest pHttpServletRequest,
                                                  HttpServletResponse pHttpServletResponse) {

        return toJsonView(OpenIdErrorType.invalid_grant, pHttpServletResponse);
    }

    @ExceptionHandler(InvalidAuthorizationCodeException.class)
    public ModelAndView invalidAuthorizationCodeException(InvalidAuthorizationCodeException pInvalidAuthorizationCodeException,
                                                          Locale pLocale,
                                                          HttpServletRequest pHttpServletRequest,
                                                          HttpServletResponse pHttpServletResponse) {

        return toJsonView(OpenIdErrorType.invalid_grant, pHttpServletResponse, pInvalidAuthorizationCodeException.getReason());
    }

    @ExceptionHandler(InvalidIntrospectException.class)
    public ModelAndView invalidIntrospectException(InvalidIntrospectException pInvalidIntrospectException,
                                                   Locale pLocale,
                                                   HttpServletRequest pHttpServletRequest,
                                                   HttpServletResponse pHttpServletResponse) {
        return toJsonView(OpenIdErrorType.invalid_request, pHttpServletResponse);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ModelAndView invalidRefreshTokenException(InvalidRefreshTokenException pInvalidRefreshTokenException,
                                                     Locale pLocale,
                                                     HttpServletRequest pHttpServletRequest,
                                                     HttpServletResponse pHttpServletResponse) {

        return toJsonView(OpenIdErrorType.invalid_grant, pHttpServletResponse, pInvalidRefreshTokenException.getReason());
    }

    private RedirectView getRedirectError(final AuthRequest pAuthRequest, OpenIdErrorType pOpenIdErrorType) {
        RedirectView view = new RedirectView();
        view.setContextRelative(false);

        // Error and state
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(UriBuilder.REDIRECTION_ERROR_KEY, pOpenIdErrorType.toString());
        if (Utils.isNotEmpty(pAuthRequest.getState())) {
            params.add(UriBuilder.PARAM_STATE, pAuthRequest.getState());
        }

        final String url = UriBuilder.build(pAuthRequest.getRedirectUri(), params, isResponseAsFragment(pAuthRequest));
        view.setUrl(url);

        return view;
    }


    private ModelAndView toJsonView(OpenIdErrorType pOpenIdErrorType,
                                    HttpServletResponse pHttpServletResponse) {
        return toJsonView(pOpenIdErrorType, pHttpServletResponse, null);
    }

    private ModelAndView toJsonView(OpenIdErrorType pOpenIdErrorType,
                                    HttpServletResponse pHttpServletResponse,
                                    String pAdditionMessage) {

        try {

            Map<String, String> models = new LinkedHashMap<>();
            models.put(UriBuilder.REDIRECTION_ERROR_KEY, pOpenIdErrorType.toString());
            if (Utils.isNotEmpty(pAdditionMessage)) {
                models.put(UriBuilder.REDIRECTION_DESC_KEY, pAdditionMessage);
            }

            final ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(pHttpServletResponse);
            outputMessage.setStatusCode(HttpStatus.BAD_REQUEST);
            jsonConverter.write(models, jsonMimeType, outputMessage);
            return null;

        } catch (IOException pIOException) {
            // Hum ?
            return toHtmlView(Utils.asMap("exception", pIOException.getMessage()));
        }

    }

    private boolean isResponseAsFragment(AuthRequest pAuthRequest) {

        return FlowUtils.RTYPE_IMPLICIT_ONLYID.equals(pAuthRequest.getResponseType()) ||
                FlowUtils.RTYPE_IMPLICIT_FULL.equals(pAuthRequest.getResponseType());

    }

    // ---


    private ModelAndView internalErrorPage(Severity pSeverity,
                                           Locale pLocale,
                                           String pErrorType,
                                           String pErrorMessage,
                                           HttpServletRequest pHttpServletRequest,
                                           HttpServletResponse pHttpServletResponse) {

        LOGGER.error("internalErrorPage: " +
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

    @Deprecated
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

    @Deprecated
    private ModelAndView toJsonView(OpenIdException pOpenIdException, HttpServletResponse pHttpServletResponse) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        try {

            Map<String, String> models = UriBuilder.buildModel(pOpenIdException);
            final ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(pHttpServletResponse);
            outputMessage.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            jsonConverter.write(models, jsonMimeType, outputMessage);
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
