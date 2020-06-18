package eu.yals.utils;

import com.bugsnag.Bugsnag;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.QueryParameters;
import eu.yals.constants.App;
import eu.yals.exception.YalsException;
import eu.yals.exception.error.UserMessageGenerator;
import eu.yals.exception.error.YalsError;
import eu.yals.exception.error.YalsErrorBuilder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static eu.yals.constants.App.NO_STATUS;

/**
 * Methods for handling application errors.
 *
 * @since 2.7
 */
@Component
public class ErrorUtils {

    private final YalsErrorKeeper errorKeeper;
    private final Bugsnag bugsnag;

    /**
     * Creates {@link ErrorUtils}.
     *
     * @param errorKeeper error holder
     * @param bugsnag     Bugsnag bean
     */
    public ErrorUtils(final YalsErrorKeeper errorKeeper, final Bugsnag bugsnag) {
        this.errorKeeper = errorKeeper;
        this.bugsnag = bugsnag;
    }

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
     * Extracts {@link YalsError} from {@link BeforeEvent}.
     *
     * @param event {@link BeforeEvent} with {@link YalsError}
     * @return extracted {@link YalsError}
     */
    public YalsError getYalsErrorFromEvent(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        if (queryParameters.getParameters().isEmpty()) return null;
        boolean errorIdKeyIsPresent = queryParameters.getParameters().containsKey(App.Params.ERROR_ID);
        if (!errorIdKeyIsPresent) return null;

        List<String> errorIdValues = queryParameters.getParameters().get(App.Params.ERROR_ID);
        boolean errorIdKeyHasSingleValue = errorIdValues.size() == 1;
        if (!errorIdKeyHasSingleValue) return null;

        String errorId = errorIdValues.get(0);
        Optional<YalsError> yalsErrorOptional = errorKeeper.get(errorId);
        return yalsErrorOptional.orElse(null);
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
     * Converts exception to {@link YalsError}.
     *
     * @param args {@link Args} object
     * @return converted {@link YalsError} object
     */
    public YalsError convertExceptionToYalsError(final ErrorUtils.Args args) {
        Throwable exceptionFromArgs = args.getException();
        boolean hasStatus = args.getStatus() != NO_STATUS;
        YalsErrorBuilder yalsErrorBuilder;

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
            yalsErrorBuilder = YalsErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            yalsErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) yalsErrorBuilder.withStatus(args.getStatus());

        } else if (exception instanceof YalsException) {
            YalsException yalsException = (YalsException) exception;

            StringBuilder techMessage = new StringBuilder(yalsException.getMessage());
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            yalsErrorBuilder = YalsErrorBuilder.withTechMessage(techMessage.toString());

            if (yalsException.hasMessageToUser()) {
                yalsErrorBuilder.withMessageToUser(yalsException.getMessageToUser());
            }

            if (hasStatus) yalsErrorBuilder.withStatus(args.getStatus());

        } else {
            //general exception
            String exceptionMessage = Objects.isNull(exception.getMessage()) ? "" : exception.getMessage();
            StringBuilder techMessage = new StringBuilder(exceptionMessage);
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            yalsErrorBuilder = YalsErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            yalsErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) yalsErrorBuilder.withStatus(args.getStatus());
        }

        yalsErrorBuilder.addRawException(exception);
        return yalsErrorBuilder.build();
    }

    /**
     * Reports issue to Bugsnag service.
     *
     * @param yalsError {@link YalsError} objects
     */
    public void reportToBugsnag(final YalsError yalsError) {
        YalsException yalsException = new YalsException("Yals Error: " + yalsError.getId());
        final String tabName = "Yals Error";
        bugsnag.addCallback(report -> {
            report.addToTab(tabName, "id", yalsError.getId());
            report.addToTab(tabName, "Timestamp", yalsError.getTimeStamp());
            report.addToTab(tabName, "Message to user", yalsError.getMessageToUser());
            report.addToTab(tabName, "Tech Message", yalsError.getTechMessage());
            report.addToTab(tabName, "HTTP Status", yalsError.getHttpStatus());
            report.addToTab(tabName, "Raw Exception", yalsError.getRawException());
        });
        bugsnag.notify(yalsException);
    }

    /**
     * Extracts deepest exception, which is root of problem.
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
         * @param status http status. See {@link eu.yals.constants.HttpCode} for more.
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
