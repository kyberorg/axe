package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import eu.yals.constants.App;
import eu.yals.exception.error.YalsError;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.ConstraintViolation;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.yals.constants.HttpCode.STATUS_500;

@Builder
@Getter
@Setter
public class YalsErrorJson implements YalsJson {
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
     * Creates {@link YalsErrorJson} with params from {@link YalsError}.
     *
     * @param error valid not empty {@link YalsError} object.
     * @return {@link YalsErrorJson} with filled fields from given {@link YalsError}.
     */
    public static YalsErrorJson createFromYalsError(final YalsError error) {
        YalsErrorJson.YalsErrorJsonBuilder json = YalsErrorJson.builder()
                .timestamp(error.getTimeStamp())
                .message(error.getMessageToUser())
                .techMessage(error.getTechMessage())
                .status(error.getHttpStatus())
                .path(error.getPath())
                .throwable(error.getRawException());
        return json.build();
    }

    /**
     * Creates {@link YalsErrorJson} from set of errors.
     *
     * @param errors errors from validator
     * @return {@link YalsErrorJson} filled with errors
     */
    @SuppressWarnings("rawtypes")
    public static YalsErrorJson createFromSetOfErrors(final Set<ConstraintViolation> errors) {
        if (errors.isEmpty()) {
            return YalsErrorJson.builder().message("Unknown error").build();
        }
        if (errors.size() == 1) {
            String error = errors.stream().findFirst()
                    .map(YalsErrorJson::convertConstraintViolationToErrorString)
                    .orElse("Unknown error");
            return YalsErrorJson.builder().message(error).build();
        } else {
            List<String> errorStrings = errors.parallelStream()
                    .map(YalsErrorJson::convertConstraintViolationToErrorString)
                    .collect(Collectors.toList());
            StringBuilder errorStr = new StringBuilder("Multiple errors detected. ");
            for (String errorString : errorStrings) {
                errorStr.append(errorString).append(App.NEW_LINE);
            }
            return YalsErrorJson.builder().message(errorStr.toString()).build();
        }
    }

    /**
     * Creates {@link YalsErrorJson} with message to user only.
     *
     * @param message string with user-friendly message
     * @return {@link YalsErrorJson}
     */
    public static YalsErrorJson createWithMessage(String message) {
        return YalsErrorJson.builder().message(message).build();
    }

    /**
     * Adds status without invoking builder.
     *
     * @param status int with http status: see {@link eu.yals.constants.HttpCode}
     * @return same {@link YalsErrorJson}, but with status
     */
    public YalsErrorJson andStatus(int status) {
        setStatus(status);
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @SuppressWarnings("rawtypes")
    private static String convertConstraintViolationToErrorString(ConstraintViolation constraintViolation) {
        if (constraintViolation == null) {
            return null;
        }
        return String.format("Field: %s, Error: %s", constraintViolation.getPropertyPath(),
                constraintViolation.getMessage());
    }
}
