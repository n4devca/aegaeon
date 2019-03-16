package ca.n4dev.aegaeon.server.controller.exception;

import ca.n4dev.aegaeon.api.protocol.AuthRequest;

/**
 * InvalidFlowException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class InvalidFlowException extends BaseException {

    private AuthRequest authRequest;

    /**
     * @param pAuthRequest
     */
    public InvalidFlowException(AuthRequest pAuthRequest) {
        authRequest = pAuthRequest;
    }

    /**
     * @return the authRequest
     */
    public AuthRequest getAuthRequest() {
        return authRequest;
    }
}
