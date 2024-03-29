package pm.axe.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import pm.axe.result.OperationResult;
import pm.axe.services.user.UserService;

/**
 * Class that generates Usernames.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UsernameGenerator {
    private static final String TAG = "[" + UsernameGenerator.class.getSimpleName() + "]";

    public static final String USER_PREFIX = "user";
    public static final int USERNAME_RANDOM_PART_LEN = 6;

    private final UserService userService;

    /**
     * Checks if username is generated by this generator.
     *
     * @param username non-empty string with username to check.
     * @return true if username is generated to this generator, false - if not.
     */
    public static boolean isGenerated(final String username) {
        return StringUtils.startsWith(username, USER_PREFIX);
    }

    /**
     * Generates new username. This method guarantees that username is unique.
     *
     * @return {@link OperationResult} with username {@link String} in payload or {@link OperationResult} with error.
     */
    public OperationResult generate() {
        try {
            String generatedUsername;
            boolean isUsernameAlreadyExist;
            do {
                generatedUsername = generatedNew();
                isUsernameAlreadyExist = userService.isUserExists(generatedUsername);
            } while (isUsernameAlreadyExist);
            return OperationResult.success().addPayload(generatedUsername);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} Exception on checking username on existence.", TAG);
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    private String generatedNew() {
        return USER_PREFIX + RandomStringUtils.randomNumeric(USERNAME_RANDOM_PART_LEN);
    }
}
