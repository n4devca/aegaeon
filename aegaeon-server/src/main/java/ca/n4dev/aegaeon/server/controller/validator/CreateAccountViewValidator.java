package ca.n4dev.aegaeon.server.controller.validator;

import ca.n4dev.aegaeon.api.validation.PasswordEvaluator;
import ca.n4dev.aegaeon.api.validation.PasswordValidity;
import ca.n4dev.aegaeon.server.utils.Utils;
import ca.n4dev.aegaeon.server.view.CreateAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * CreateAccountViewValidator.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 18 - 2018
 */
@Component
public class CreateAccountViewValidator implements Validator {

    private PasswordEvaluator passwordEvaluator;

    @Autowired
    public CreateAccountViewValidator(PasswordEvaluator pPasswordEvaluator) {
        this.passwordEvaluator = pPasswordEvaluator;
    }

    @Override
    public boolean supports(Class<?> pClass) {
        return CreateAccountView.class.equals(pClass);
    }

    /*
    page.signup.error.username.empty=Username is required
page.signup.error.username.notunique=This username is already used. Please select another one.
page.signup.error.passwordconfirm.empty=Password confirmation is required
page.signup.error.passwd.notequals=Password are not the same.
page.signup.error.passwd.insecured=This password is insecured. Try to have an upper case letter, a number and/or a special character.
    * */

    @Override
    public void validate(Object pCreateAccountView, Errors pErrors) {
        CreateAccountView createAccountView = (CreateAccountView) pCreateAccountView;

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "username",
                                                  "page.signup.error.username.empty",
                                                  "Username is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "password",
                                                  "page.signup.error.password.empty",
                                                  "Password is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "passwordConfirm",
                                                  "page.signup.error.passwordconfirm.empty",
                                                  "Password is required.");

        if (!Utils.equals(createAccountView.getPassword(), createAccountView.getPasswordConfirm())) {
            pErrors.rejectValue("password", "page.signup.error.passwd.notequals");
            pErrors.rejectValue("passwordConfirm", "page.signup.error.passwd.notequals");
        }

        PasswordValidity validity = this.passwordEvaluator.evaluate(createAccountView.getPassword());
        if (!validity.isValid()) {
            pErrors.rejectValue("password", validity.getReason());
        }
    }
}
