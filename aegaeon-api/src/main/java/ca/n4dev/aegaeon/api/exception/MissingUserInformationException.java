package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;

/**
 * MissingUserInformationException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 11 - 2019
 */
public class MissingUserInformationException extends BaseException {

    private ClientRequest clientRequest;

    public MissingUserInformationException(ClientRequest pClientRequest) {
        clientRequest = pClientRequest;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }
}
