package ca.n4dev.aegaeon.server.controller.exception;

/**
 * MissingInformationException.java
 * <p>
 * Exception throw when a field is missing.
 *
 * @author rguillemette
 * @since 2.0.0 - Mar 09 - 2019
 */
public class MissingInformationException extends BaseException {

    private String missingField;

    public MissingInformationException(String pMissingField) {
        missingField = pMissingField;
    }

    /**
     * @return the missingField
     */
    public String getMissingField() {
        return missingField;
    }

    /**
     * @param pMissingField the missingField to set
     */
    public void setMissingField(String pMissingField) {
        missingField = pMissingField;
    }
}
