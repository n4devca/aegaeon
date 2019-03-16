package ca.n4dev.aegaeon.server.controller.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * InvalidScopeException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class InvalidScopeException extends BaseException {

    private String scope;
    private ClientRequest clientRequest;

    public InvalidScopeException(String pScope, ClientRequest pClientRequest) {
        scope = pScope;
        clientRequest = pClientRequest;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }
}
