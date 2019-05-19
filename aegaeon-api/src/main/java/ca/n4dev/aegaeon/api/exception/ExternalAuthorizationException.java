package ca.n4dev.aegaeon.api.exception;

/**
 * ExternalAuthorizationException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - May 17 - 2019
 */
public class ExternalAuthorizationException extends BaseException {

    private String code;

    public ExternalAuthorizationException(String pCode, String message) {
        super(message);
        code = pCode;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
}
