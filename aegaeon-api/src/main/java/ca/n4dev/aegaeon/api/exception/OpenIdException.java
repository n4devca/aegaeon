package ca.n4dev.aegaeon.api.exception;

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
    private OAuthErrorType error;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error_uri")
    private String errorUri;

    @JsonIgnore
    private String username;

    @JsonIgnore
    private String clientPublicId;

    @JsonIgnore
    private Class<?> source;

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
    public OpenIdException(ServerExceptionCode pServerExceptionCode, String pClientPublicId, Class<?> pSource) {
        super(pServerExceptionCode);
        this.clientPublicId = pClientPublicId;
        this.source = pSource;
    }

    /**
     * @return the error
     */
    public OAuthErrorType getError() {
        return error;
    }

    /**
     * @param pError the error to set
     */
    public void setError(OAuthErrorType pError) {
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
}
