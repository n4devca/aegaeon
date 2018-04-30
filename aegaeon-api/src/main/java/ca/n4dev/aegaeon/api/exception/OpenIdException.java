package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.GrantType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OpenIdException.java
 *
 * Class describing an exception related to an openid operation.
 *
 * @author rguillemette
 * @since 2.0.0 - Apr 10 - 2018
 */
public class OpenIdException extends ServerException {

    @JsonProperty("error")
    private OpenIdErrorType error;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error_uri")
    private String errorUri;

    @JsonIgnore
    private String username;

    @JsonIgnore
    private Class<?> source;

    @JsonIgnore
    private String clientPublicId;

    @JsonIgnore
    private String clientState;

    @JsonIgnore
    private String clientUri;

    @JsonIgnore
    private ErrorHandling errorHandling;

    @JsonIgnore
    private GrantType requestedGrantType;


    /**
     * Basic Constructor.
     * @param pServerExceptionCode The internal server error code.
     */
    public OpenIdException(ServerExceptionCode pServerExceptionCode) {
        super(pServerExceptionCode);
    }

    /**
     * Basic Constructor.
     * @param pServerExceptionCode The internal server error code.
     */
    public OpenIdException(ServerExceptionCode pServerExceptionCode, Throwable pThrowable) {
        super(pServerExceptionCode);
    }

    /**
     * Basic Constructor.
     * @param pServerExceptionCode The internal server error code.
     */
    public OpenIdException(ServerExceptionCode pServerExceptionCode, String pClientPublicId, Class<?> pSource) {
        super(pServerExceptionCode);
        this.clientPublicId = pClientPublicId;
        this.source = pSource;
    }

    /**
     * @return the error
     */
    public OpenIdErrorType getError() {
        return error;
    }

    /**
     * @param pError the error to set
     */
    public void setError(OpenIdErrorType pError) {
        error = pError;
    }

    /**
     * @return the errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * @param pErrorDescription the errorDescription to set
     */
    public void setErrorDescription(String pErrorDescription) {
        errorDescription = pErrorDescription;
    }

    /**
     * @return the errorUri
     */
    public String getErrorUri() {
        return errorUri;
    }

    /**
     * @param pErrorUri the errorUri to set
     */
    public void setErrorUri(String pErrorUri) {
        errorUri = pErrorUri;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param pUsername the username to set
     */
    public void setUsername(String pUsername) {
        username = pUsername;
    }

    /**
     * @return the clientPublicId
     */
    public String getClientPublicId() {
        return clientPublicId;
    }

    /**
     * @param pClientPublicId the clientPublicId to set
     */
    public void setClientPublicId(String pClientPublicId) {
        clientPublicId = pClientPublicId;
    }

    /**
     * @return the source
     */
    public Class<?> getSource() {
        return source;
    }

    /**
     * @param pSource the source to set
     */
    public void setSource(Class<?> pSource) {
        source = pSource;
    }

    /**
     * @return the clientState
     */
    public String getClientState() {
        return clientState;
    }

    /**
     * @param pClientState the clientState to set
     */
    public void setClientState(String pClientState) {
        clientState = pClientState;
    }

    /**
     * @return the clientUri
     */
    public String getClientUri() {
        return clientUri;
    }

    /**
     * @param pClientUri the clientUri to set
     */
    public void setClientUri(String pClientUri) {
        clientUri = pClientUri;
    }

    /**
     * @return the errorHandling
     */
    public ErrorHandling getErrorHandling() {
        return errorHandling;
    }

    /**
     * @return the requestedGrantType
     */
    public GrantType getRequestedGrantType() {
        return requestedGrantType;
    }

    /**
     * @param pRequestedGrantType the requestedGrantType to set
     */
    public void setRequestedGrantType(GrantType pRequestedGrantType) {
        requestedGrantType = pRequestedGrantType;
    }

    /**
     * @param pErrorHandling the errorHandling to set
     */
    public void setErrorHandling(ErrorHandling pErrorHandling) {
        errorHandling = pErrorHandling;
    }

}
