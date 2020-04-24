package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.yals.json.internal.Json;
import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This struct of JSON send in case of message.
 *
 * @since 1.0
 */
public class ErrorJson extends Json {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Getter
    private Error error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Getter
    private final List<Error> errors = new ArrayList<>(2);

    private static final String NO_MESSAGE = "No error message found";

    /**
     * Static constructor, build object with error message.
     *
     * @param errorMsg non-empty string contains error message
     * @return error json object
     */
    public static ErrorJson createWithMessage(@NotNull final String errorMsg) {
        ErrorJson errorJson = new ErrorJson();
        errorJson.error = Error.create().message(errorMsg);
        return errorJson;
    }

    /**
     * Static constructor, build object from provided set of errors.
     *
     * @param errorSet error objects
     * @return error json object
     */
    @SuppressWarnings("rawtypes")
    public static ErrorJson createFromSetOfErrors(@NotNull final Set<ConstraintViolation> errorSet) {
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

    /**
     * JSON field and error message combination.
     */
    public static class Error {
        @Getter
        private String field;

        @Getter
        private String errorMessage;

        /**
         * Creates blank {@link Error} object.
         *
         * @return no params {@link Error}
         */
        static Error create() {
            return new Error();
        }

        /**
         * Create {@link Error} object from given {@link ConstraintViolation} object.
         * Extracts it to field and message.
         *
         * @param violation not empty object, which contains field and error message
         * @return error object with given params
         */
        @SuppressWarnings("rawtypes")
        private static Error create(@NotNull final ConstraintViolation violation) {
            Error error = new Error();
            if (Objects.nonNull(violation.getPropertyPath())) {
                error.field = violation.getPropertyPath().toString();
            }
            error.message(violation.getMessage());
            return error;
        }

        private Error message(final String errorText) {
            this.field = "";
            this.errorMessage = errorText;
            return this;
        }
    }
}
