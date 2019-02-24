package ca.n4dev.aegaeon.server.controller.validator;

import ca.n4dev.aegaeon.server.controller.dto.ChangeUsernameDto;
import ca.n4dev.aegaeon.server.service.UserService;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * ChangeUsernameValidator.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Jun 15 - 2018
 */
@Component
public class ChangeUsernameValidator implements Validator {

    private UserService userService;

    /**
     * Constructor.
     * @param pUserService The user service.
     */
    @Autowired
    public ChangeUsernameValidator(UserService pUserService) {
        userService = pUserService;
    }

    @Override
    public boolean supports(Class<?> pClass) {
        return ChangeUsernameDto.class.equals(pClass);
    }

    @Override
    public void validate(Object pO, Errors pErrors) {

        ChangeUsernameDto dto = (ChangeUsernameDto) pO;

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "newUsername",
                                                  "page.chgusername.error.mandatory.username",
                                                  "New user name is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(pErrors, "newUsernameConfirm",
                                                  "page.chgusername.error.mandatory.usernameconfirm",
                                                  "User name's confirmation is required.");

        if (!Utils.equals(dto.getNewUsername(), dto.getNewUsernameConfirm())) {
            pErrors.rejectValue("newUsername", "page.chgusername.error.notequals");
            pErrors.rejectValue("newUsernameConfirm", "page.signup.error.passwd.notequals");
        }

        // Check if username already exists
        if (Utils.isNotEmpty(dto.getNewUsername()) && userService.existsByUserName(dto.getNewUsername())) {
            pErrors.rejectValue("newUsername", "page.chgusername.error.alreadyexists");
        }
    }
}
