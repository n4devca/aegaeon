package ca.n4dev.aegaeon.api.validation;

/**
 * PasswordValidity.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 18 - 2018
 */
public interface PasswordValidity {

    /**
     * @return if the password is valid.
     */
    boolean isValid();

    /**
     * @return A reason why this password is invalid.
     */
    String getReason();

    /**
     * @return A number between 0 and 100 (higher is better) representing password strength.
     */
    int getStrength();
}
