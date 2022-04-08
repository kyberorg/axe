package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.error.YalseeError;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    private int status = HttpCode.SERVER_ERROR;

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
    public static YalseeErrorJson createFromYalseeError(final YalseeError error) {
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

}
