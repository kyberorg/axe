package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import eu.yals.exception.error.YalsError;
import lombok.Data;

import java.util.Date;

import static eu.yals.constants.HttpCode.STATUS_500;

@Data(staticConstructor = "withMessage")
public class YalsErrorJson implements YalsJson {
    @JsonProperty("timestamp")
    private String timestamp = new Date().toString();

    @JsonProperty("message")
    private final String message;

    @JsonProperty("tech_message")
    private final String techMessage;

    @JsonProperty("status")
    private int status = STATUS_500;

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

    @JsonProperty("path")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

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
        YalsErrorJson json = YalsErrorJson.withMessage(error.getMessageToUser(), error.getTechMessage());
        json.setThrowable(error.getRawException());
        json.timestamp = error.getTimeStamp();
        json.status = error.getHttpStatus();
        json.path = error.getPath();
        return json;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
