package io.kyberorg.yalsee.exception.error;

import io.kyberorg.yalsee.constants.HttpCode;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder for {@link YalseeError}.
 *
 * @since 2.7
 */
@SuppressWarnings("UnusedReturnValue") //normal for Builder
public final class YalseeErrorBuilder {
    private String messageToUser;
    private final String techMessage;
    private int httpStatus = HttpCode.SERVER_ERROR;
    private Throwable rawException = null;

    /**
     * Adds technical message to {@link YalseeError}. Intended as {@link YalseeErrorBuilder} start point.
     *
     * @param techMessage not empty string with tech message
     * @return {@link YalseeErrorBuilder} object to continue building {@link YalseeError}
     */
    public static YalseeErrorBuilder withTechMessage(final String techMessage) {
        return new YalseeErrorBuilder(techMessage);
    }

    private YalseeErrorBuilder(final String technicalMessage) {
        this.techMessage = technicalMessage;
    }

    /**
     * Adds user-friendly message.
     *
     * @param userFriendlyMessage non-empty string with message to user.
     * @return {@link YalseeErrorBuilder} object to continue building {@link YalseeError}
     */
    public YalseeErrorBuilder withMessageToUser(final String userFriendlyMessage) {
        this.messageToUser = userFriendlyMessage;
        return this;
    }

    /**
     * Adds http status.
     *
     * @param status int with valid {@link HttpCode}
     * @return {@link YalseeErrorBuilder} object to continue building {@link YalseeError}
     */
    public YalseeErrorBuilder withStatus(final int status) {
        this.httpStatus = status;
        return this;
    }

    /**
     * Adds exception object.
     *
     * @param throwable {@link Throwable} object with exception caused issue.
     * @return {@link YalseeErrorBuilder} object to continue building {@link YalseeError}
     */
    public YalseeErrorBuilder addRawException(final Throwable throwable) {
        this.rawException = throwable;
        return this;
    }

    /**
     * Triggers build, which makes {@link YalseeError} from {@link YalseeErrorBuilder}.
     *
     * @return created {@link YalseeError} object.
     */
    public YalseeError build() {
        YalseeError yalseeError = new YalseeError();
        if (StringUtils.isNotBlank(techMessage)) {
            yalseeError.setTechMessage(techMessage);
        }

        if (StringUtils.isNotBlank(messageToUser)) {
            yalseeError.setMessageToUser(messageToUser);
        }
        yalseeError.setHttpStatus(httpStatus);

        if (rawException != null) {
            yalseeError.setRawException(rawException);
        }

        return yalseeError;
    }
}
