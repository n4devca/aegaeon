package ca.n4dev.aegaeon.server.controller.exception;

import java.util.Collections;
import java.util.Set;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * UnauthorizedScope.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 11 - 2019
 */
public class UnauthorizedScope extends BaseException {

    private ClientRequest clientRequest;
    private Set<String> unauthorizedScopes;

    public UnauthorizedScope(ClientRequest pClientRequest, Set<String> pUnauthorizedScopes) {
        clientRequest = pClientRequest;
        unauthorizedScopes = Collections.unmodifiableSet(pUnauthorizedScopes);
    }

    /**
     * @return the authRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }

    /**
     * @return the unauthorizedScopes
     */
    public Set<String> getUnauthorizedScopes() {
        return unauthorizedScopes;
    }
}
