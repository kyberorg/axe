package pm.axe.utils;

import com.bugsnag.Bugsnag;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.QueryParameters;
import kong.unirest.HttpStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pm.axe.Axe;
import pm.axe.exception.AxeException;
import pm.axe.exception.error.AxeError;
import pm.axe.exception.error.AxeErrorBuilder;
import pm.axe.exception.error.UserMessageGenerator;
import pm.axe.mail.LetterType;
import pm.axe.services.mail.MailSenderService;

import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Methods for handling application errors.
 *
 * @since 2.7
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ErrorUtils {
    private static final String TAG = "[" + ErrorUtils.class.getSimpleName() + "]";
    private static final int SERVER_ERROR_HTTP_STATUS = 500;
    private final AxeErrorKeeper errorKeeper;
    private final Bugsnag bugsnag;
    private final AppUtils appUtils;
    private final MailSenderService mailSenderService;

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
     * Extracts {@link AxeError} from {@link BeforeEvent}.
     *
     * @param event {@link BeforeEvent} with {@link AxeError}
     * @return extracted {@link AxeError}
     */
    public AxeError getAxeErrorFromEvent(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        if (queryParameters.getParameters().isEmpty()) return null;
        boolean errorIdKeyIsPresent = queryParameters.getParameters().containsKey(Axe.Params.ERROR_ID);
        if (!errorIdKeyIsPresent) return null;

        List<String> errorIdValues = queryParameters.getParameters().get(Axe.Params.ERROR_ID);
        boolean errorIdKeyHasSingleValue = errorIdValues.size() == 1;
        if (!errorIdKeyHasSingleValue) return null;

        String errorId = errorIdValues.get(0);
        Optional<AxeError> axeErrorOptional = errorKeeper.get(errorId);
        return axeErrorOptional.orElse(null);
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
     * Converts exception to {@link AxeError}.
     *
     * @param args {@link Args} object
     * @return converted {@link AxeError} object
     */
    public AxeError convertExceptionToAxeError(final ErrorUtils.Args args) {
        Throwable exceptionFromArgs = args.getException();
        boolean hasStatus = args.getStatus() != Axe.C.NO_STATUS;
        AxeErrorBuilder axeErrorBuilder;

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
            axeErrorBuilder = AxeErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            axeErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) axeErrorBuilder.withStatus(args.getStatus());

        } else if (exception instanceof AxeException axeException) {

            StringBuilder techMessage = new StringBuilder(axeException.getMessage());
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            axeErrorBuilder = AxeErrorBuilder.withTechMessage(techMessage.toString());

            if (axeException.hasMessageToUser()) {
                axeErrorBuilder.withMessageToUser(axeException.getMessageToUser());
            }

            if (hasStatus) axeErrorBuilder.withStatus(args.getStatus());

        } else {
            //general exception
            String exceptionMessage = Objects.isNull(exception.getMessage()) ? "" : exception.getMessage();
            StringBuilder techMessage = new StringBuilder(exceptionMessage);
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            axeErrorBuilder = AxeErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            axeErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) axeErrorBuilder.withStatus(args.getStatus());
        }

        axeErrorBuilder.addRawException(exception);
        return axeErrorBuilder.build();
    }

    /**
     * Reports issue to Bugsnag service.
     *
     * @param axeError {@link AxeError} objects
     */
    public void reportToBugsnag(final AxeError axeError) {
        AxeException axeException = new AxeException("Axe Error: " + axeError.getId());
        final String tabName = "Axe Error";
        bugsnag.addCallback(report -> {
            report.addToTab(tabName, "id", axeError.getId());
            report.addToTab(tabName, "Timestamp", axeError.getTimeStamp());
            report.addToTab(tabName, "Message to user", axeError.getMessageToUser());
            report.addToTab(tabName, "Tech Message", axeError.getTechMessage());
            report.addToTab(tabName, "HTTP Status", axeError.getHttpStatus());
            report.addToTab(tabName, "Raw Exception", axeError.getRawException());
        });
        bugsnag.notify(axeException);
        if (axeError.getHttpStatus() >= SERVER_ERROR_HTTP_STATUS) {
            notifyByEmail(axeError);
        }
    }

    /**
     * Reports issue to Maintainer's email.
     *
     * @param axeError {@link AxeError} object
     */
    public void notifyByEmail(final AxeError axeError) {
        String emailForErrors = appUtils.getEmailForErrors();
        if (emailForErrors.equals(Axe.C.NO_VALUE)) {
            log.warn("{} failed to notify about server error by email. Reason: email for errors is not set", TAG);
            return;
        }
        String subject = "Error Report";
        String jsonizedAxeError = AppUtils.GSON.toJson(axeError);

        Map<String, Object> templateVars = new HashMap<>(1);
        templateVars.put("axeError", jsonizedAxeError);

        try {
            MimeMessage letter =
                    mailSenderService.createLetter(LetterType.SERVER_ERROR, emailForErrors, subject, templateVars);
            mailSenderService.sendEmail(emailForErrors, letter);
        } catch (Exception e) {
            log.error("{} failed to create or send error report email. Got exception {}",
                    TAG, e.getClass().getSimpleName());
            log.error("", e);
        }
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
        boolean hasStatus = args.getStatus() != Axe.C.NO_STATUS;
        boolean hasPath = StringUtils.isNotBlank(args.getPath());
        if (hasStatus) {
            techMessage.append(Axe.C.NEW_LINE).append("Status: ").append(args.getStatus());
        }
        if (hasPath) {
            techMessage.append(Axe.C.NEW_LINE).append("Path: ").append(args.getPath());
        }
        techMessage.trimToSize();
    }

    @Data
    public static class Args {
        private Throwable exception;
        private int status = Axe.C.NO_STATUS;
        private String path;
    }

    public static class ArgsBuilder {
        private Throwable th;
        private int status = Axe.C.NO_STATUS;
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
         * @param status http status. See {@link HttpStatus} for more.
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
            if (status != Axe.C.NO_STATUS) {
                args.status = status;
            }
            if (StringUtils.isNotBlank(path)) {
                args.path = path;
            }
            return args;
        }
    }
}
