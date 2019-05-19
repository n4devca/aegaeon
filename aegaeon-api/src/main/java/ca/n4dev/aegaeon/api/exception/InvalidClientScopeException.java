package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * InvalidClientScopeException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - May 17 - 2019
 */
public class InvalidClientScopeException extends BaseException {

    private final ClientRequest clientRequest;

    public InvalidClientScopeException(ClientRequest pClientRequest) {
        clientRequest = pClientRequest;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }
}
