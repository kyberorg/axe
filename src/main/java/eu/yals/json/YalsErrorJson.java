package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.annotations.Since;
import lombok.Data;

import java.util.Date;

@Data(staticConstructor = "withMessage")
@Since(2.7)
public class YalsErrorJson {
    private String timestamp = new Date().toString();
    private final String message;
    private int status = 500;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Throwable throwable = null;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
