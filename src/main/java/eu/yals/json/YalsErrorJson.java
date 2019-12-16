package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.annotations.Since;
import eu.yals.exception.error.YalsError;
import lombok.Data;

import java.util.Date;

@Data(staticConstructor = "withMessage")
@Since(2.7)
public class YalsErrorJson {
    private String timestamp = new Date().toString();
    private final String message;
    private final String techMessage;
    private int status = 500;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Throwable throwable = null;

    public static YalsErrorJson createFromYalsError(YalsError error) {
        YalsErrorJson json = YalsErrorJson.withMessage(error.getMessageToUser(), error.getTechMessage());
        json.setThrowable(error.getRawException());
        json.timestamp = error.getTimeStamp();
        json.status  = error.getHttpStatus();
        return json;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
