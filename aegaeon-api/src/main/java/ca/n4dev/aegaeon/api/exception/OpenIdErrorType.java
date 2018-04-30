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
package ca.n4dev.aegaeon.api.exception;

/**
 * OpenIdErrorType.java
 * 
 * A list of errors that can be returned by the server.
 *
 * Based on :
 * http://openid.net/specs/openid-connect-core-1_0.html
 * https://tools.ietf.org/html/rfc6749
 *
 * @author by rguillemette
 * @since May 22, 2017
 */
public enum OpenIdErrorType {

    // The request is missing a required parameter, includes an invalid parameter value, includes a parameter more than
    // once, or is otherwise malformed.
    invalid_request,

    //The client is not authorized to request an authorization code using this method.
    unauthorized_client,

    // The resource owner or authorization server denied the request.
    access_denied,

    // The authorization server does not support obtaining an authorization code using this method.
    unsupported_response_type,

    // The requested scope is invalid, unknown, or malformed.
    invalid_scope,

    // The authorization server encountered an unexpected condition that prevented it from fulfilling the request.
    server_error,

    // The authorization server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
    temporarily_unavailable,

    // The Authorization Server requires End-User authentication. This error MAY be returned when the prompt parameter value in the Authentication
    // Request is none, but the Authentication Request cannot be completed without displaying a user interface for End-User authentication.
    login_required,

    // The Authorization Server requires End-User interaction of some form to proceed.
    // This error MAY be returned when the prompt parameter value in the Authentication
    // Request is none, but the Authentication Request cannot be completed without displaying a user interface for End-User interaction.
    interaction_required,

    // The End-User is REQUIRED to select a session at the Authorization Server. The End-User MAY be authenticated at the Authorization
    // Server with different associated accounts, but the End-User did not select a session. This error MAY be returned when the prompt
    // parameter value in the Authentication Request is none, but the Authentication Request cannot be completed without displaying
    // a user interface to prompt for a session to use.
    account_selection_required,

    // The Authorization Server requires End-User consent. This error MAY be returned when the prompt parameter value in the Authentication
    // Request is none, but the Authentication Request cannot be completed without displaying a user interface for End-User consent.
    consent_required,

    // The request_uri in the Authorization Request returns an error or contains invalid data.
    invalid_request_uri,

    // The request parameter contains an invalid Request Object.
    invalid_request_object,

    // The OP does not support use of the request parameter defined in Section 6.
    request_not_supported,

    // The OP does not support use of the request_uri parameter defined in Section 6.
    request_uri_not_supported,

    // The OP does not support use of the registration parameter defined in Section 7.2.1.
    registration_not_supported
    ;

    public static OpenIdErrorType fromServerCode(ServerExceptionCode pServerExceptionCode) {
        if (pServerExceptionCode != null) {
            switch (pServerExceptionCode) {
                case CLIENT_UNAUTHORIZED_FLOW:
                    return unauthorized_client;

                case AUTH_CODE_EXPIRED:
                    return interaction_required;

                case AUTH_CODE_EMPTY:
                    return invalid_request;

                case AUTH_CODE_UNEXPECTED_CLIENT:
                    return access_denied;

                case AUTH_CODE_UNEXPECTED_REDIRECTION:
                    return access_denied;

                case CLIENT_EMPTY:
                    return access_denied;

                case CLIENT_UNAUTHORIZED_SCOPE:
                    return consent_required;

                case CLIENT_REDIRECTIONURL_INVALID:
                    return access_denied;

                case GRANT_INVALID:
                    return consent_required;

                case REFRESH_TOKEN_EMPTY:
                    return invalid_request;

                case REFRESH_TOKEN_EXPIRED:
                    return interaction_required;

                case USER_UNAUTHENTICATED:
                    return login_required;

                case RESPONSETYPE_INVALID:
                    return unsupported_response_type;
            }
        }

        return server_error;
    }
}
