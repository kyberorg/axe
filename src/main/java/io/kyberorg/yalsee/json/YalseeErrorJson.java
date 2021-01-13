package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.error.YalseeError;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.ConstraintViolation;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_500;

@Builder
@Getter
@Setter
public class YalseeErrorJson implements YalseeJson {
    @Builder.Default
    @JsonProperty("timestamp")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String timestamp = new Date().toString();

    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonProperty("tech_message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String techMessage;

    @Builder.Default
    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private int status = STATUS_500;

    @JsonProperty("path")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

    @Builder.Default
    @JsonProperty("throwable")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Throwable throwable = null;

    /**
     * Creates {@link YalseeErrorJson} with params from {@link YalseeError}.
     *
     * @param error valid not empty {@link YalseeError} object.
     * @return {@link YalseeErrorJson} with filled fields from given {@link YalseeError}.
     */
    public static YalseeErrorJson createFromYalsError(final YalseeError error) {
        YalseeErrorJson.YalseeErrorJsonBuilder json = YalseeErrorJson.builder()
                .timestamp(error.getTimeStamp())
                .message(error.getMessageToUser())
                .techMessage(error.getTechMessage())
                .status(error.getHttpStatus())
                .path(error.getPath())
                .throwable(error.getRawException());
        return json.build();
    }

    /**
     * Creates {@link YalseeErrorJson} from set of errors.
     *
     * @param errors errors from validator
     * @return {@link YalseeErrorJson} filled with errors
     */
    @SuppressWarnings("rawtypes")
    public static YalseeErrorJson createFromSetOfErrors(final Set<ConstraintViolation> errors) {
        if (errors.isEmpty()) {
            return YalseeErrorJson.builder().message("Unknown error").build();
        }
        if (errors.size() == 1) {
            String error = errors.stream().findFirst()
                    .map(YalseeErrorJson::convertConstraintViolationToErrorString)
                    .orElse("Unknown error");
            return YalseeErrorJson.builder().message(error).build();
        } else {
            List<String> errorStrings = errors.parallelStream()
                    .map(YalseeErrorJson::convertConstraintViolationToErrorString)
                    .collect(Collectors.toList());
            StringBuilder errorStr = new StringBuilder("Multiple errors detected. ");
            for (String errorString : errorStrings) {
                errorStr.append(errorString).append(App.NEW_LINE);
            }
            return YalseeErrorJson.builder().message(errorStr.toString()).build();
        }
    }

    /**
     * Creates {@link YalseeErrorJson} with message to user only.
     *
     * @param message string with user-friendly message
     * @return {@link YalseeErrorJson}
     */
    public static YalseeErrorJson createWithMessage(final String message) {
        return YalseeErrorJson.builder().message(message).build();
    }

    /**
     * Adds status without invoking builder.
     *
     * @param status int with http status: see {@link HttpCode}
     * @return same {@link YalseeErrorJson}, but with status
     */
    public YalseeErrorJson andStatus(final int status) {
        setStatus(status);
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @SuppressWarnings("rawtypes")
    private static String convertConstraintViolationToErrorString(final ConstraintViolation constraintViolation) {
        if (constraintViolation == null) {
            return null;
        }
        return String.format("Field: %s, Error: %s", constraintViolation.getPropertyPath(),
                constraintViolation.getMessage());
    }
}
