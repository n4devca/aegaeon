package ca.n4dev.aegaeon.api.exception;

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

    public MissingInformationException(String pMissingField, String pMessage) {
        super(pMessage);
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
