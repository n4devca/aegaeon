package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.ClientRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * InvalidRequestMethodException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public class InvalidRequestMethodException extends BaseException {

    private ClientRequest clientRequest;

    private RequestMethod requestMethod;

    public InvalidRequestMethodException(ClientRequest pClientRequest, RequestMethod pRequestMethod) {
        clientRequest = pClientRequest;
        requestMethod = pRequestMethod;
    }

    /**
     * @return the clientRequest
     */
    public ClientRequest getClientRequest() {
        return clientRequest;
    }

    /**
     * @return the requestMethod
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

}
