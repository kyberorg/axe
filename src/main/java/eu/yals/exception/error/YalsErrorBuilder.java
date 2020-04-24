package eu.yals.exception.error;

import org.apache.commons.lang3.StringUtils;

import static eu.yals.constants.HttpCode.STATUS_500;

/**
 * Builder for {@link YalsError}.
 *
 * @since 2.7
 */
@SuppressWarnings("UnusedReturnValue") //normal for Builder
public final class YalsErrorBuilder {
    private String messageToUser;
    private final String techMessage;
    private int httpStatus = STATUS_500;
    private Throwable rawException = null;

    /**
     * Adds technical message to {@link YalsError}. Intended as {@link YalsErrorBuilder} start point.
     *
     * @param techMessage not empty string with tech message
     * @return {@link YalsErrorBuilder} object to continue building {@link YalsError}
     */
    public static YalsErrorBuilder withTechMessage(final String techMessage) {
        return new YalsErrorBuilder(techMessage);
    }

    private YalsErrorBuilder(final String technicalMessage) {
        this.techMessage = technicalMessage;
    }

    /**
     * Adds user-friendly message.
     *
     * @param userFriendlyMessage non-empty string with message to user.
     * @return {@link YalsErrorBuilder} object to continue building {@link YalsError}
     */
    public YalsErrorBuilder withMessageToUser(final String userFriendlyMessage) {
        this.messageToUser = userFriendlyMessage;
        return this;
    }

    /**
     * Adds http status.
     *
     * @param status int with valid {@link eu.yals.constants.HttpCode}
     * @return {@link YalsErrorBuilder} object to continue building {@link YalsError}
     */
    public YalsErrorBuilder withStatus(final int status) {
        this.httpStatus = status;
        return this;
    }

    /**
     * Adds exception object.
     *
     * @param throwable {@link Throwable} object with exception caused issue.
     * @return {@link YalsErrorBuilder} object to continue building {@link YalsError}
     */
    public YalsErrorBuilder addRawException(final Throwable throwable) {
        this.rawException = throwable;
        return this;
    }

    /**
     * Triggers build, which makes {@link YalsError} from {@link YalsErrorBuilder}.
     *
     * @return created {@link YalsError} object.
     */
    public YalsError build() {
        YalsError yalsError = new YalsError();
        if (StringUtils.isNotBlank(techMessage)) {
            yalsError.setTechMessage(techMessage);
        }

        if (StringUtils.isNotBlank(messageToUser)) {
            yalsError.setMessageToUser(messageToUser);
        }
        yalsError.setHttpStatus(httpStatus);

        if (rawException != null) {
            yalsError.setRawException(rawException);
        }

        return yalsError;
    }
}
