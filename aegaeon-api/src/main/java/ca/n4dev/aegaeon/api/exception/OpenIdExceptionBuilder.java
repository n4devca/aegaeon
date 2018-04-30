package ca.n4dev.aegaeon.api.exception;

import ca.n4dev.aegaeon.api.protocol.GrantType;

/**
 * OpenIdExceptionBuilder.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Apr 17 - 2018
 */
public class OpenIdExceptionBuilder {

    private OpenIdException exception;

    public OpenIdExceptionBuilder() {
        this(ServerExceptionCode.UNEXPECTED_ERROR);
    }

    public OpenIdExceptionBuilder(ServerExceptionCode pServerExceptionCode) {
        exception = new OpenIdException(pServerExceptionCode);
    }

    public OpenIdExceptionBuilder(Exception pException) {

        ServerExceptionCode code = pException instanceof ServerException ?
                ((ServerException) pException).getCode() : ServerExceptionCode.UNEXPECTED_ERROR;
        OpenIdException openIdException = new OpenIdException(code, pException.getCause());

        exception = openIdException;
    }

    public OpenIdExceptionBuilder(OpenIdException pOpenIdException) {
        exception = pOpenIdException;
    }

    public OpenIdExceptionBuilder clientId(String pClientId) {
        exception.setClientPublicId(pClientId);
        return this;
    }

    public OpenIdExceptionBuilder redirection(String pClientRedirectionUri) {
        exception.setClientUri(pClientRedirectionUri);
        return this;
    }

    public OpenIdExceptionBuilder state(String pClientState) {
        exception.setClientState(pClientState);
        return this;
    }

    public OpenIdExceptionBuilder description(String pDescription) {
        exception.setErrorDescription(pDescription);
        return this;
    }

    public OpenIdExceptionBuilder handling(ErrorHandling pErrorHandling) {
        exception.setErrorHandling(pErrorHandling);
        return this;
    }

    public OpenIdExceptionBuilder code(ServerExceptionCode pServerExceptionCode) {
        exception.setCode(pServerExceptionCode);
        return this;
    }

    public OpenIdExceptionBuilder from(GrantType pGrantType) {
        exception.setRequestedGrantType(pGrantType);
        return this;
    }

    public OpenIdException build() {
        OpenIdException oe = exception;
        exception = null;
        return oe;
    }
}
