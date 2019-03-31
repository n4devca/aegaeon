package ca.n4dev.aegaeon.server.event;

import java.util.Set;

import ca.n4dev.aegaeon.server.view.ScopeView;
import org.springframework.context.ApplicationEvent;

/**
 * UserInfoEvent.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 30 - 2019
 */
public class UserInfoEvent extends ApplicationEvent {

    private String clientId;
    private Set<ScopeView> scopes;
    private String userId;

    public UserInfoEvent(Object source, String pClientId, Set<ScopeView> pScopes, String pUserId) {
        super(source);
        clientId = pClientId;
        scopes = pScopes;
        userId = pUserId;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return the scopes
     */
    public Set<ScopeView> getScopes() {
        return scopes;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }
}
