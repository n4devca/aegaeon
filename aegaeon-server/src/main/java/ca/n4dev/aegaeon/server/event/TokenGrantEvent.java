package ca.n4dev.aegaeon.server.event;

import org.springframework.context.ApplicationEvent;

/**
 * TokenGrantEvent.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 31 - 2019
 */
public class TokenGrantEvent extends ApplicationEvent {

    private String clientId;
    private String requestedScope;
    private String allowedScope;
    private String code;
    private String userId;
    private String grantType;
    private boolean idToken;
    private boolean accessToken;
    private boolean refreshToken;

    public TokenGrantEvent(Object source, String pClientId, String pRequestedScope, String pAllowedScope, String pCode,
                           String pUserId, String pGrantType, boolean pIdToken, boolean pAccessToken, boolean pRefreshToken) {
        super(source);
        clientId = pClientId;
        requestedScope = pRequestedScope;
        allowedScope = pAllowedScope;
        code = pCode;
        userId = pUserId;
        grantType = pGrantType;
        idToken = pIdToken;
        accessToken = pAccessToken;
        refreshToken = pRefreshToken;
    }

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
     * @return the requestedScope
     */
    public String getRequestedScope() {
        return requestedScope;
    }

    /**
     * @param pRequestedScope the requestedScope to set
     */
    public void setRequestedScope(String pRequestedScope) {
        requestedScope = pRequestedScope;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param pUserId the userId to set
     */
    public void setUserId(String pUserId) {
        userId = pUserId;
    }

    /**
     * @return the grantType
     */
    public String getGrantType() {
        return grantType;
    }

    /**
     * @param pGrantType the grantType to set
     */
    public void setGrantType(String pGrantType) {
        grantType = pGrantType;
    }

    /**
     * @return the allowedScope
     */
    public String getAllowedScope() {
        return allowedScope;
    }

    /**
     * @param pAllowedScope the allowedScope to set
     */
    public void setAllowedScope(String pAllowedScope) {
        allowedScope = pAllowedScope;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param pCode the code to set
     */
    public void setCode(String pCode) {
        code = pCode;
    }

    /**
     * @return the idToken
     */
    public boolean isIdToken() {
        return idToken;
    }

    /**
     * @param pIdToken the idToken to set
     */
    public void setIdToken(boolean pIdToken) {
        idToken = pIdToken;
    }

    /**
     * @return the accessToken
     */
    public boolean isAccessToken() {
        return accessToken;
    }

    /**
     * @param pAccessToken the accessToken to set
     */
    public void setAccessToken(boolean pAccessToken) {
        accessToken = pAccessToken;
    }

    /**
     * @return the refreshToken
     */
    public boolean isRefreshToken() {
        return refreshToken;
    }

    /**
     * @param pRefreshToken the refreshToken to set
     */
    public void setRefreshToken(boolean pRefreshToken) {
        refreshToken = pRefreshToken;
    }
}
