package ee.yals.json;

import com.google.gson.annotations.Since;

import javax.validation.constraints.NotNull;

/**
 * This struct of JSON send in case of message
 *
 * @since 1.0
 */
@Since(1.0)
public class ErrorJson extends Json {

    @Since(1.0)
    private Error error;

    public static ErrorJson createWithMessage(@NotNull String errorMsg) {
        ErrorJson errorJson = new ErrorJson();
        errorJson.error = Error.create().message(errorMsg);
        return errorJson;
    }

    public Error getError() {
        return error;
    }

    @Since(1.0)
    public static class Error {
        @Since(1.0)
        String errorMessage;

        static Error create() {
            return new Error();
        }

        Error message(String errorText) {
            this.errorMessage = errorText;
            return this;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
