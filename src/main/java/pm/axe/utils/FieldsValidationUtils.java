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

    public boolean isUsernameInvalid(final TextField usernameInput) {
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

    public boolean isEmailInvalid(final TextField emailInput) {
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

    public boolean isPasswordInvalid(final PasswordField passwordInput) {
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
