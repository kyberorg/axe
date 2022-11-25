package pm.axe.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link RuntimeException} with message as technical message and custom message as message for user.
 *
 * @since 2.7
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AxeException extends RuntimeException {

    private String messageToUser;

    /**
     * Creates empty exception without message to user.
     */
    public AxeException() {
        super();
    }

    /**
     * Creates exception with message to user.
     *
     * @param message string with user-friendly message
     */
    public AxeException(final String message) {
        super(message);
    }

    /**
     * Checks if {@link AxeException} object has not-empty {@link #messageToUser}.
     *
     * @return true - when {@link #messageToUser} contains non-empty string, false - elsewhere
     */
    public boolean hasMessageToUser() {
        return StringUtils.isNotBlank(messageToUser);
    }
}
