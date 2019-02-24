package ca.n4dev.aegaeon.server.controller.validator;

import ca.n4dev.aegaeon.api.validation.PasswordEvaluator;
import ca.n4dev.aegaeon.api.validation.PasswordValidity;
import ca.n4dev.aegaeon.server.controller.dto.ChangePasswdDto;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * ChangePasswordValidator.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Jun 10 - 2018
 */
@Component
public class ChangePasswordValidator implements Validator {

    private PasswordEvaluator passwordEvaluator;

    @Autowired
    public ChangePasswordValidator(PasswordEvaluator pPasswordEvaluator) {
        passwordEvaluator = pPasswordEvaluator;
    }

    @Override
    public boolean supports(Class<?> pClass) {
        return ChangePasswdDto.class.equals(pClass);
    }

    @Override
    public void validate(Object pO, Errors pErrors) {

        ChangePasswdDto dto = (ChangePasswdDto) pO;

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "oldPassword",
                                                  "page.chgpasswd.error.oldpasswd.empty",
                                                  "Current password is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "newPassword",
                                                  "page.chgpasswd.error.newpasswd.empty",
                                                  "New password is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "newPasswordConfirm",
                                                  "page.chgpasswd.error.newpasswdconfirm.empty",
                                                  "Password confirmation is required.");

        if (!Utils.equals(dto.getNewPassword(), dto.getNewPasswordConfirm())) {
            pErrors.rejectValue("newPassword", "page.signup.error.passwd.notequals");
            pErrors.rejectValue("newPasswordConfirm", "page.signup.error.passwd.notequals");
        }

        PasswordValidity validity = this.passwordEvaluator.evaluate(dto.getNewPassword());
        if (!validity.isValid()) {
            pErrors.rejectValue("newPassword", validity.getReason());
        }
    }
}
