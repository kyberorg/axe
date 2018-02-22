package ee.yals.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.yals.json.internal.Json;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * App-wide tools
 *
 * @since 1.0
 */
public class AppUtils {

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private AppUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates host:port server running at
     *
     * @since 2.0
     */
    public static class HostHelper {
        private HostHelper() {
            throw new UnsupportedOperationException("Utility class");
        }

        public static String getAPIHostPort() {
            return "localhost" + ":" + System.getProperty("server.port", "8080");
        }
    }

    /**
     * Helps with incoming JSON validation. Validation based on Annotations.
     */
    public static class ValidationHelper {

        public static final Set<ConstraintViolation> EMPTY_SET = Collections.unmodifiableSet(new HashSet<>());

        private ValidationHelper() {
            throw new UnsupportedOperationException("Utility class");
        }

        /**
         * @param incomingJson json to validate
         * @return {@link Set} with errors or {@link ValidationHelper#EMPTY_SET}
         */
        public static Set<ConstraintViolation> validateJsonFields(Json incomingJson) {
            final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<Json>> errorsFromValidator = validator.validate(incomingJson);
            if (!errorsFromValidator.isEmpty()) {
                return new HashSet<>(errorsFromValidator);
            } else {
                return EMPTY_SET;
            }
        }
    }
}
