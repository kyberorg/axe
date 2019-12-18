package eu.yals.utils;

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

import java.util.List;
import java.util.Optional;

@Component
public class ErrorUtils {

    private YalsErrorKeeper errorKeeper;

    public ErrorUtils(YalsErrorKeeper errorKeeper) {
        this.errorKeeper = errorKeeper;
    }

    public YalsError getYalsErrorFromEvent(BeforeEvent event) {
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

    public int parseStatusFromErrorParameter(ErrorParameter<? extends Exception> parameter, int defaultStatus) {
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

    public YalsError convertExceptionToYalsError(ErrorUtils.Args args) {
        Throwable exception = args.getException();
        boolean hasStatus = args.getStatus() != Args.NO_STATUS;
        YalsErrorBuilder yalsErrorBuilder;

        if (exception == null) {
            //tech message
            StringBuilder techMessage = new StringBuilder("Not exceptional situation.");
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
            StringBuilder techMessage = new StringBuilder(exception.getMessage());
            this.enrichTechMessageWithStatusAndPath(techMessage, args);

            yalsErrorBuilder = YalsErrorBuilder.withTechMessage(techMessage.toString());

            //user message based on status
            yalsErrorBuilder.withMessageToUser(UserMessageGenerator.getMessageByStatus(args.getStatus()));

            if (hasStatus) yalsErrorBuilder.withStatus(args.getStatus());
        }

        yalsErrorBuilder.addRawException(exception);
        return yalsErrorBuilder.build();
    }

    private void enrichTechMessageWithStatusAndPath(StringBuilder techMessage, Args args) {
        boolean hasStatus = args.getStatus() != Args.NO_STATUS;
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
        public static final int NO_STATUS = -1;

        Throwable exception;
        int status = NO_STATUS;
        String path;
    }

    public static class ArgsBuilder {
        private Throwable th;
        private int status = Args.NO_STATUS;
        private String path;

        public static ArgsBuilder withException(Throwable th) {
            ArgsBuilder builder = new ArgsBuilder();
            builder.th = th;
            return builder;
        }

        public ArgsBuilder addStatus(int status) {
            this.status = status;
            return this;
        }

        public ArgsBuilder addPath(String path) {
            this.path = path;
            return this;
        }

        public Args build() {
            Args args = new Args();
            args.exception = th;
            if (status != Args.NO_STATUS) {
                args.status = status;
            }
            if (StringUtils.isNotBlank(path)) {
                args.path = path;
            }
            return args;
        }
    }
}
