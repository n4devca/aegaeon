package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.TokenRequest;

/**
 * InvalidAuthorizationCodeException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 13 - 2019
 */
public class InvalidAuthorizationCodeException extends BaseException {

    public static final String EMPTY = "EMPTY";
    public static final String EXPIRED = "EXPIRED";
    public static final String INVALID_CLIENT = "INVALID_CLIENT";
    public static final String INVALID_CLIENT_URI = "INVALID_CLIENT_URI";
    public static final String EMPTY_USER = "EMPTY_USER";

    private String reason;
    private TokenRequest tokenRequest;

    public InvalidAuthorizationCodeException(TokenRequest pTokenRequest, String pReason) {
        tokenRequest = pTokenRequest;
        reason = pReason;
    }

    /**
     * @return the tokenRequest
     */
    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
}
