package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.TokenRequest;

/**
 * InvalidRefreshTokenException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 14 - 2019
 */
public class InvalidRefreshTokenException extends BaseException {

    public static final String EMPTY = "EMPTY";
    public static final String EXPIRED = "EXPIRED";
    public static final String INVALID_CLIENT = "INVALID_CLIENT";

    private TokenRequest tokenRequest;

    private String reason;

    public InvalidRefreshTokenException(TokenRequest pTokenRequest, String pReason) {
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
