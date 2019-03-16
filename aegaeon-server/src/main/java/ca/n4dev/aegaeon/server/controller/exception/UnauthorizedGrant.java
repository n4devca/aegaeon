package ca.n4dev.aegaeon.server.controller.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * UnauthorizedGrant.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 13 - 2019
 */
public class UnauthorizedGrant extends BaseException {

    private ClientRequest clientRequest;

    public UnauthorizedGrant(ClientRequest pClientRequest) {
        clientRequest = pClientRequest;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }
}
