package ca.n4dev.aegaeon.server.controller.exception;

import ca.n4dev.aegaeon.api.protocol.TokenRequest;

/**
 * InvalidGrantTypeException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class InvalidGrantTypeException extends BaseException {

    private TokenRequest tokenRequest;

    public InvalidGrantTypeException(TokenRequest pTokenRequest) {
        tokenRequest = pTokenRequest;
    }

    /**
     * @return the tokenRequest
     */
    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }

    /**
     * @param pTokenRequest the tokenRequest to set
     */
    public void setTokenRequest(TokenRequest pTokenRequest) {
        tokenRequest = pTokenRequest;
    }
}
