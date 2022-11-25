package pm.axe.exception.error;

import pm.axe.constants.HttpCode;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder for {@link AxeError}.
 *
 * @since 2.7
 */
@SuppressWarnings("UnusedReturnValue") //normal for Builder
public final class AxeErrorBuilder {
    private String messageToUser;
    private final String techMessage;
    private int httpStatus = HttpCode.SERVER_ERROR;
    private Throwable rawException = null;

    /**
     * Adds technical message to {@link AxeError}. Intended as {@link AxeErrorBuilder} start point.
     *
     * @param techMessage not empty string with tech message
     * @return {@link AxeErrorBuilder} object to continue building {@link AxeError}
     */
    public static AxeErrorBuilder withTechMessage(final String techMessage) {
        return new AxeErrorBuilder(techMessage);
    }

    private AxeErrorBuilder(final String technicalMessage) {
        this.techMessage = technicalMessage;
    }

    /**
     * Adds user-friendly message.
     *
     * @param userFriendlyMessage non-empty string with message to user.
     * @return {@link AxeErrorBuilder} object to continue building {@link AxeError}
     */
    public AxeErrorBuilder withMessageToUser(final String userFriendlyMessage) {
        this.messageToUser = userFriendlyMessage;
        return this;
    }

    /**
     * Adds http status.
     *
     * @param status int with valid {@link HttpCode}
     * @return {@link AxeErrorBuilder} object to continue building {@link AxeError}
     */
    public AxeErrorBuilder withStatus(final int status) {
        this.httpStatus = status;
        return this;
    }

    /**
     * Adds exception object.
     *
     * @param throwable {@link Throwable} object with exception caused issue.
     * @return {@link AxeErrorBuilder} object to continue building {@link AxeError}
     */
    public AxeErrorBuilder addRawException(final Throwable throwable) {
        this.rawException = throwable;
        return this;
    }

    /**
     * Triggers build, which makes {@link AxeError} from {@link AxeErrorBuilder}.
     *
     * @return created {@link AxeError} object.
     */
    public AxeError build() {
        AxeError axeError = new AxeError();
        if (StringUtils.isNotBlank(techMessage)) {
            axeError.setTechMessage(techMessage);
        }

        if (StringUtils.isNotBlank(messageToUser)) {
            axeError.setMessageToUser(messageToUser);
        }
        axeError.setHttpStatus(httpStatus);

        if (rawException != null) {
            axeError.setRawException(rawException);
        }

        return axeError;
    }
}
