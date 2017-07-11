package ee.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Since;
import ee.yals.json.internal.Json;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This struct of JSON send in case of message
 *
 * @since 1.0
 */
@Since(1.0)
public class ErrorJson extends Json {

    @Since(1.0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Error error;

    @Since(1.0)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Error> errors = new ArrayList<>(2);

    private static final String NO_MESSAGE = "No error message found";

    public static ErrorJson createWithMessage(@NotNull String errorMsg) {
        ErrorJson errorJson = new ErrorJson();
        errorJson.error = Error.create().message(errorMsg);
        return errorJson;
    }

    public static ErrorJson createFromSetOfErrors(@NotNull Set<ConstraintViolation> errorSet) {
        if (errorSet.isEmpty()) {
            return createWithMessage(NO_MESSAGE);
        }

        ErrorJson errorJson = new ErrorJson();

        for (ConstraintViolation error : errorSet) {
            Error e = Error.create(error);
            errorJson.errors.add(e);
        }

        if (errorJson.errors.size() == 1) {
            errorJson.error = errorJson.errors.get(0);
            errorJson.errors.clear();
        }

        return errorJson;
    }


    public Error getError() {
        return error;
    }

    public List<Error> getErrors(){
        return errors;
    }

    @Since(1.0)
    public static class Error {
        @Since(1.0)
        String field;

        @Since(1.0)
        String errorMessage;

        static Error create() {
            return new Error();
        }

        static Error create(@NotNull ConstraintViolation violation) {
            Error error = new Error();
            if (Objects.nonNull(violation.getPropertyPath())) {
                error.field = violation.getPropertyPath().toString();
            }
            error.message(violation.getMessage());
            return error;
        }

        Error message(String errorText) {
            this.field = "";
            this.errorMessage = errorText;
            return this;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getField(){
            return field;
        }
    }
}
