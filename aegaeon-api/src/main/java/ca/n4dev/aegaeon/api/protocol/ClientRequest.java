package ca.n4dev.aegaeon.api.protocol;

/**
 * BaseRequest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 10 - 2019
 */
public abstract class ClientRequest {

    protected String clientId;

    protected String redirectUri;

    protected String state;

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param pClientId the clientId to set
     */
    public void setClientId(String pClientId) {
        clientId = pClientId;
    }

    /**
     * @return the redirectUri
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * @param pRedirectUri the redirectUri to set
     */
    public void setRedirectUri(String pRedirectUri) {
        redirectUri = pRedirectUri;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param pState the state to set
     */
    public void setState(String pState) {
        state = pState;
    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "clientId='" + clientId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                '}';
    }
}
