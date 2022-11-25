package pm.axe.internal;

import pm.axe.services.user.UserOperationsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Incoming params for {@link UserOperationsService#registerUser(RegisterUserInput)}.
 */
@RequiredArgsConstructor
@Data
public class RegisterUserInput {
    private final String email;
    private final String username;
    @ToString.Exclude
    private final String password;
    private final boolean tfaEnabled;
}
