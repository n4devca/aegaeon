package ca.n4dev.aegaeon.server.event;

import org.springframework.context.ApplicationEvent;

/**
 * IntrospectEvent.java
 * <p>
 * A event raised when a client introspect a token.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 30 - 2019
 */
public class IntrospectEvent extends ApplicationEvent {

    private String userId;
    private String clientId;
    private boolean clientAllowed;
    private String valueReturned;


    /**
     * Create the event.
     *
     * @param source
     * @param pUserId
     * @param pClientId
     * @param pClientAllowed
     * @param pResult
     */
    public IntrospectEvent(Object source, String pClientId, boolean pClientAllowed, String pUserId, String pResult) {
        super(source);
        clientId = pClientId;
        clientAllowed = pClientAllowed;
        valueReturned = pResult;
        userId = pUserId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return the clientAllowed
     */
    public boolean isClientAllowed() {
        return clientAllowed;
    }

    /**
     * @return the valueReturned
     */
    public String getValueReturned() {
        return valueReturned;
    }
}
