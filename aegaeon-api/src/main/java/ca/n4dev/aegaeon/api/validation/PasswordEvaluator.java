package ca.n4dev.aegaeon.api.validation;

/**
 * PasswordEvaluator.java
 *
 * Offer a common interface to component evaluating password.
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 18 - 2018
 */
public interface PasswordEvaluator {

    /**
     * Evaluate a password.
     * @param pPassword
     * @return An evaluation of this password.
     */
    PasswordValidity evaluate(String pPassword);
}
