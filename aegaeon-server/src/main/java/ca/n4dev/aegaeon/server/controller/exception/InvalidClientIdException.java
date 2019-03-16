package ca.n4dev.aegaeon.server.controller.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * InvalidClientIdException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class InvalidClientIdException extends BaseException {

    private ClientRequest clientRequest;

    public InvalidClientIdException(ClientRequest pClientRequest) {
        clientRequest = pClientRequest;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }
}
