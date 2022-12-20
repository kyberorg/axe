package pm.axe.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import kong.unirest.HttpStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pm.axe.exception.error.AxeError;

import java.util.Date;

@Builder
@Getter
@Setter
public class AxeErrorJson implements AxeJson {
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
    private int status = HttpStatus.INTERNAL_SERVER_ERROR;

    @JsonProperty("path")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

    @Builder.Default
    @JsonProperty("throwable")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Throwable throwable = null;

    /**
     * Creates {@link AxeErrorJson} with params from {@link AxeError}.
     *
     * @param error valid not empty {@link AxeError} object.
     * @return {@link AxeErrorJson} with filled fields from given {@link AxeError}.
     */
    public static AxeErrorJson createFromAxeError(final AxeError error) {
        AxeErrorJson.AxeErrorJsonBuilder json = AxeErrorJson.builder()
                .timestamp(error.getTimeStamp())
                .message(error.getMessageToUser())
                .techMessage(error.getTechMessage())
                .status(error.getHttpStatus())
                .path(error.getPath())
                .throwable(error.getRawException());
        return json.build();
    }

    /**
     * Creates {@link AxeErrorJson} with message to user only.
     *
     * @param message string with user-friendly message
     * @return {@link AxeErrorJson}
     */
    public static AxeErrorJson createWithMessage(final String message) {
        return AxeErrorJson.builder().message(message).build();
    }

    /**
     * Adds status without invoking builder.
     *
     * @param status int with http status: see {@link HttpStatus}
     * @return same {@link AxeErrorJson}, but with status
     */
    public AxeErrorJson andStatus(final int status) {
        setStatus(status);
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
