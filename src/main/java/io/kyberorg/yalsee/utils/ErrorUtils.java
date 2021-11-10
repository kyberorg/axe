package io.kyberorg.yalsee.utils;

import com.bugsnag.Bugsnag;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.QueryParameters;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.YalseeException;
import io.kyberorg.yalsee.exception.error.UserMessageGenerator;
import io.kyberorg.yalsee.exception.error.YalseeError;
import io.kyberorg.yalsee.exception.error.YalseeErrorBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.kyberorg.yalsee.constants.App.NO_STATUS;

/**
 * Methods for handling application errors.
 *
 * @since 2.7
 */
@AllArgsConstructor
@Component
public class ErrorUtils {

    private final YalseeErrorKeeper errorKeeper;
    private final Bugsnag bugsnag;

    /**
     * Converts from stack trace to String with stack trace.
     *
     * @param e exception with stack trace
     * @return stack trace as string
     */
    public static String stackTraceToString(final Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Creates Error Modal in the middle of page.
     *
     * @param text string with error
     * @return created {@link Notification}
     */
    public static Notification getErrorNotification(final String text) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return AppUtils.getNotification(text, NotificationVariant.LUMO_ERROR);
    }

    /**
     * Extracts {@link YalseeError} from {@link BeforeEvent}.
     *
     * @param event {@link BeforeEvent} with {@link YalseeError}
     * @return extracted {@link YalseeError}
     */
    public YalseeError getYalseeErrorFromEvent(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        if (queryParameters.getParameters().isEmpty()) return null;
        boolean errorIdKeyIsPresent = queryParameters.getParameters().containsKey(App.Params.ERROR_ID);
        if (!errorIdKeyIsPresent) return null;

        List<String> errorIdValues = queryParameters.getParameters().get(App.Params.ERROR_ID);
        boolean errorIdKeyHasSingleValue = errorIdValues.size() == 1;
        if (!errorIdKeyHasSingleValue) return null;

        String errorId = errorIdValues.get(0);
        Optional<YalseeError> yalseeErrorOptional = errorKeeper.get(errorId);
        return yalseeErrorOptional.orElse(null);
    }

    /**
     * Gets status from {@link ErrorParameter} object.
     *
     * @param parameter     error parameter
     * @param defaultStatus status to result, if no status found in parameter
     * @return status found from {@link ErrorParameter} or defaultStatus
     */
    public int parseStatusFromErrorParameter(final ErrorParameter<? extends Exception> parameter,
                                             final int defaultStatus) {
        int status;
        if (parameter != null && parameter.hasCustomMessage()) {
            String statusString = parameter.getCustomMessage();
            try {
                status = Integer.parseInt(statusString);
            } catch (Exception e) {
                status = defaultStatus;
            }
        } else {
            status = defaultStatus;
        }
        return status;
    }

    /**
     * Converts exception to {@link YalseeError}.
     *
     * @param args {@link Args} object
     * @return converted {@link YalseeError} object
     */
    public YalseeError convertExceptionToYalseeError(final ErrorUtils.Args args) {
        Throwable exceptionFromArgs = args.getException();
        boolean hasStatus = args.getStatus() != NO_STATUS;
        YalseeErrorBuilder yalseeErrorBuilder;

        Throwable exception = findRootCause(exceptionFromArgs);

        if (exception == null) {
            //tech message
            StringBuilder techMessage = new StringBuilder();
            if (hasStatus) {
                techMessage.append(UserMessageGenerator.getMessageByStatus(args.getStatus()));
            } else {
                techMessage.append("Not exceptional situation. Something went wrong.");
            }

            this.enrichTechMessageWithStatusAndPath(techMessage, args);
            yalseeErrorBuilder = YalseeErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            yalseeErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) yalseeErrorBuilder.withStatus(args.getStatus());

        } else if (exception instanceof YalseeException) {
            YalseeException yalseeException = (YalseeException) exception;

            StringBuilder techMessage = new StringBuilder(yalseeException.getMessage());
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            yalseeErrorBuilder = YalseeErrorBuilder.withTechMessage(techMessage.toString());

            if (yalseeException.hasMessageToUser()) {
                yalseeErrorBuilder.withMessageToUser(yalseeException.getMessageToUser());
            }

            if (hasStatus) yalseeErrorBuilder.withStatus(args.getStatus());

        } else {
            //general exception
            String exceptionMessage = Objects.isNull(exception.getMessage()) ? "" : exception.getMessage();
            StringBuilder techMessage = new StringBuilder(exceptionMessage);
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            yalseeErrorBuilder = YalseeErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            yalseeErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) yalseeErrorBuilder.withStatus(args.getStatus());
        }

        yalseeErrorBuilder.addRawException(exception);
        return yalseeErrorBuilder.build();
    }

    /**
     * Reports issue to Bugsnag service.
     *
     * @param yalseeError {@link YalseeError} objects
     */
    public void reportToBugsnag(final YalseeError yalseeError) {
        YalseeException yalseeException = new YalseeException("Yalsee Error: " + yalseeError.getId());
        final String tabName = "Yalsee Error";
        bugsnag.addCallback(report -> {
            report.addToTab(tabName, "id", yalseeError.getId());
            report.addToTab(tabName, "Timestamp", yalseeError.getTimeStamp());
            report.addToTab(tabName, "Message to user", yalseeError.getMessageToUser());
            report.addToTab(tabName, "Tech Message", yalseeError.getTechMessage());
            report.addToTab(tabName, "HTTP Status", yalseeError.getHttpStatus());
            report.addToTab(tabName, "Raw Exception", yalseeError.getRawException());
        });
        bugsnag.notify(yalseeException);
    }

    /**
     * Extracts the deepest exception, which is root of problem.
     *
     * @param throwable exception containing chain of exceptions
     * @return deepest exception from the chain
     */
    public static Throwable findRootCause(final Throwable throwable) {
        if (throwable == null) return null;
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private void enrichTechMessageWithStatusAndPath(final StringBuilder techMessage, final Args args) {
        boolean hasStatus = args.getStatus() != NO_STATUS;
        boolean hasPath = StringUtils.isNotBlank(args.getPath());
        if (hasStatus) {
            techMessage.append(App.NEW_LINE).append("Status: ").append(args.getStatus());
        }
        if (hasPath) {
            techMessage.append(App.NEW_LINE).append("Path: ").append(args.getPath());
        }
        techMessage.trimToSize();
    }

    @Data
    public static class Args {
        private Throwable exception;
        private int status = NO_STATUS;
        private String path;
    }

    public static class ArgsBuilder {
        private Throwable th;
        private int status = NO_STATUS;
        private String path;

        /**
         * Builder static constructor creates object with provided exception.
         *
         * @param th exception
         * @return {@link ArgsBuilder} object
         */
        public static ArgsBuilder withException(final Throwable th) {
            ArgsBuilder builder = new ArgsBuilder();
            builder.th = th;
            return builder;
        }

        /**
         * Add status.
         *
         * @param status http status. See {@link HttpCode} for more.
         * @return {@link ArgsBuilder} object
         */
        public ArgsBuilder addStatus(final int status) {
            this.status = status;
            return this;
        }

        /**
         * Add endpoint path, which caused an error.
         *
         * @param path string with valid endpoint path, which caused an error
         * @return {@link ArgsBuilder} object
         */
        public ArgsBuilder addPath(final String path) {
            this.path = path;
            return this;
        }

        /**
         * Builds {@link Args} object from fields in Builder.
         *
         * @return built {@link Args}
         */
        public Args build() {
            Args args = new Args();
            args.exception = th;
            if (status != NO_STATUS) {
                args.status = status;
            }
            if (StringUtils.isNotBlank(path)) {
                args.path = path;
            }
            return args;
        }
    }
}
