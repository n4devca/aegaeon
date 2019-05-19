package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.AuthRequest;

/**
 * InternalAuthorizationException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class InternalAuthorizationException extends BaseException {

    private AuthRequest authRequest;

    public InternalAuthorizationException(AuthRequest pAuthRequest, Throwable pCause) {
        super(pCause);
        authRequest = pAuthRequest;
    }

    public InternalAuthorizationException(AuthRequest pAuthRequest, String pMessage) {
        super(pMessage);
        authRequest = pAuthRequest;
    }

    /**
     * @return the authRequest
     */
    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    /**
     * @param pAuthRequest the authRequest to set
     */
    public void setAuthRequest(AuthRequest pAuthRequest) {
        authRequest = pAuthRequest;
    }

}
