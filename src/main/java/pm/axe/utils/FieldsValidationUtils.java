package pm.axe.utils;

import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import pm.axe.result.OperationResult;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.UserService;
import pm.axe.users.AccountType;
import pm.axe.users.PasswordValidator;

import static pm.axe.utils.VaadinUtils.onInvalidInput;


@RequiredArgsConstructor
@Component
@UIScope
public class FieldsValidationUtils {
    private final AccountService accountService;
    private final UserService userService;

    /**
     * Checks if username is valid or not.
     *
     * @param usernameInput {@link TextField} with username.
     * @return true if username is valid, false if not.
     * @throws IllegalArgumentException when usernameInput is NULL
     */
    public boolean isUsernameInvalid(final TextField usernameInput) {
        if (usernameInput == null) throw new IllegalArgumentException("usernameInput cannot be NULL");
        String userOrEmail = usernameInput.getValue().trim();
        boolean isInputEmpty = StringUtils.isBlank(userOrEmail);
        if (isInputEmpty) {
            return true;
        }

        boolean isUserExists = userService.isUserExists(userOrEmail);
        if (!isUserExists) {
            onInvalidInput(usernameInput, "No such user found");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if email address is valid or not.
     *
     * @param emailInput {@link TextField} with email address.
     * @return true if email address is valid, false if not.
     * @throws IllegalArgumentException when emailInput is NULL
     */
    public boolean isEmailInvalid(final TextField emailInput) {
        if (emailInput == null) throw new IllegalArgumentException("email input cannot be null");
        final String email = emailInput.getValue().trim();
        boolean isEmail = EmailValidator.getInstance().isValid(email);
        if (!isEmail) {
            onInvalidInput(emailInput, "Given string is not email");
            return true;
        }
        boolean emailExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
        if (!emailExists) {
            onInvalidInput(emailInput, "No user with given email found");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if password is valid or not.
     *
     * @param passwordInput {@link PasswordField} with password to check
     * @return true if password is valid, false - is not.
     * @throws IllegalArgumentException when passwordInput is NULL
     */
    public boolean isPasswordInvalid(final PasswordField passwordInput) {
        if (passwordInput == null) throw new IllegalArgumentException("password input cannot be null");
        String password = passwordInput.getValue().trim();
        OperationResult passwordValidationOp = PasswordValidator.isPasswordValid(password);
        if (passwordValidationOp.notOk()) {
            String errorMessage = passwordValidationOp.getMessage();
            onInvalidInput(passwordInput, errorMessage);
            return true;
        } else {
            return false;
        }
    }
}
