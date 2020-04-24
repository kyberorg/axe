package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import eu.yals.exception.error.YalsError;
import lombok.Data;

import java.util.Date;

import static eu.yals.constants.HttpCode.STATUS_500;

@Data(staticConstructor = "withMessage")
public class YalsErrorJson {
    private String timestamp = new Date().toString();
    private final String message;
    private final String techMessage;
    private int status = STATUS_500;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

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
        return json;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
