package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * UserUnauthorizedException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - May 16 - 2019
 */
public class UserUnauthorizedException extends BaseException {

    private final ClientRequest clientRequest;

    public UserUnauthorizedException(ClientRequest pClientRequest) {
        clientRequest = pClientRequest;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }
}
