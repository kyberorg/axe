package pm.axe.result;

import com.google.gson.internal.Primitives;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class that defines result of operation. Operation can be any action: validation, query from database and so on.
 *
 * @since 3.0.4
 */
@ToString
public class OperationResult {

    /**
     * Status, that indicates operation success.
     */
    public static final String OK = "OP_OK";

    /**
     * Status, that indicates that provided params are incorrect.
     */
    public static final String MALFORMED_INPUT = "OP_WRONG_INPUT";

    /**
     * Status, that indicates failure.
     */
    public static final String GENERAL_FAIL = "OP_GENERAL_FAIL";

    /**
     * Status for events where searched element was not found.
     */
    public static final String ELEMENT_NOT_FOUND = "OP_NO_ELEM";

    /**
     * Status, that indicates partial or complete Application failure.
     */
    public static final String SYSTEM_DOWN = "OP_SYSTEM_DOWN";

    /**
     * Status raised, when attempted to store something that already exists.
     */
    public static final String CONFLICT = "OP_CONFLICT";

    /**
     * Status, that indicated that access to requested resource or action is not allowed.
     */
    public static final String BANNED = "OP_NO_ACCESS";

    /**
     * Status, that indicated that referenced object is no longer exists.
     */
    public static final String GONE = "OP_GONE";

    private static final String DEFAULT_PAYLOAD_KEY = "DEFAULT_PAYLOAD_KEY";

    @Getter
    private String result;
    @Getter
    private String message;
    private final Map<String, Object> payload = new HashMap<>(1);

    /**
     * Default constructor.
     */
    protected OperationResult() {
    }

    /**
     * Constructs object with given status.
     *
     * @param status string with one of given statuses.
     */
    private OperationResult(final String status) {
        this.result = status;
    }

    /**
     * Create object with {@link #GENERAL_FAIL} status.
     *
     * @return new object.
     */
    public static OperationResult generalFail() {
        return new OperationResult(GENERAL_FAIL);
    }

    /**
     * Create object with {@link #MALFORMED_INPUT} status.
     *
     * @return new object.
     */
    public static OperationResult malformedInput() {
        return new OperationResult(MALFORMED_INPUT);
    }

    /**
     * Create object with {@link #OK} status.
     *
     * @return new object.
     */
    public static OperationResult success() {
        return new OperationResult(OK);
    }

    /**
     * Create object with {@link #SYSTEM_DOWN} status and 'Database is DOWN' message.
     *
     * @return new object.
     */
    public static OperationResult databaseDown() {
        OperationResult result = new OperationResult(SYSTEM_DOWN);
        result.message = "Database is DOWN";
        return result;
    }

    /**
     * Create object with {@link #ELEMENT_NOT_FOUND} status.
     *
     * @return new object.
     */
    public static OperationResult elementNotFound() {
        return new OperationResult(ELEMENT_NOT_FOUND);
    }

    /**
     * Create object with {@link #CONFLICT} status.
     *
     * @return new object.
     */
    public static OperationResult conflict() {
        return new OperationResult(CONFLICT);
    }

    /**
     * Create object with {@link #BANNED} status.
     *
     * @return new object.
     */
    public static OperationResult banned() {
        return new OperationResult(BANNED);
    }

    /**
     * Create object with {@link #GONE} status.
     *
     * @return new object.
     */
    public static OperationResult gone() {
        return new OperationResult(GONE);
    }

    /**
     * Checks if operation executed successfully.
     *
     * @return true - if all good, false if not
     */
    public boolean ok() {
        return result.equals(OK);
    }

    /**
     * Checks if operation failed.
     *
     * @return opposite to {@link #ok()}
     */
    public boolean notOk() {
        return !ok();
    }

    /**
     * Adds custom message.
     *
     * @param customMessage string with message.
     * @return same object, but enriched with {@link #message}
     */
    public OperationResult withMessage(final String customMessage) {
        this.message = customMessage;
        return this;
    }

    /**
     * Adds payload produced by operation.
     *
     * @param payload operation output object
     * @return same object, but with added {@link #payload}
     */
    public OperationResult addPayload(final Object payload) {
        this.payload.put(DEFAULT_PAYLOAD_KEY, payload);
        return this;
    }

    /**
     * Stores payload under given key.
     *
     * @param key     key to store payload under.
     * @param payload operation output object
     * @return same object, but with added {@link #payload}
     * @throws IllegalArgumentException if key is {@code null} or empty.
     */
    public OperationResult addPayload(final String key, final Object payload) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        this.payload.put(key, payload);
        return this;
    }

    /**
     * Returns payload as a {@link String}.
     *
     * @return string stored in payload.
     * @throws ClassCastException when payload contains something else but not {@link String}.
     */
    public String getStringPayload() {
        return getPayload(String.class);
    }

    /**
     * Returns payload value, stored under given key as a {@link String}.
     *
     * @param key string with key payload stored under.
     * @return string store in payload.
     * @throws ClassCastException       when payload contains something else but not {@link String}.
     * @throws IllegalArgumentException if key is {@code null} or empty.
     */
    public String getStringPayload(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        return getPayload(key, String.class);
    }

    /**
     * Returns payload value with requested class.
     * For example: {@link #getPayload(Long)} will return {@link Long} object.
     *
     * @param classOfPayload class of object stored in {@link #payload}
     * @param <T>            Java generics param.
     * @return object stored in {@link #payload} converted to requested class.
     * @throws ClassCastException when payload contains something else, but not object of requested class.
     */
    @SuppressWarnings("JavadocReference") //dynamic class param and example uses exact class to show usage.
    public <T> T getPayload(final Class<T> classOfPayload) {
        return Primitives.wrap(classOfPayload).cast(this.payload.get(DEFAULT_PAYLOAD_KEY));
    }

    /**
     * Returns payload value, stored under given key, cast to requested class.
     * For example: {@link #getPayload(String, Long)} will return {@link Long} object.
     *
     * @param key            string with key payload stored under.
     * @param classOfPayload class of object stored in {@link #payload}
     * @param <T>            Java generics param.
     * @return object stored in {@link #payload} converted to requested class.
     * @throws ClassCastException       when payload contains something else, but not object of requested class.
     * @throws IllegalArgumentException if key is {@code null} or empty.
     */
    @SuppressWarnings("JavadocReference") //dynamic class param and example uses exact class to show usage.
    public <T> T getPayload(final String key, final Class<T> classOfPayload) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        return Primitives.wrap(classOfPayload).cast(this.payload.get(key));
    }

    /**
     * Check if Payload contains something stored under given Key.
     *
     * @param key not-empty string with key to check.
     * @return true - if {@link #payload} contains given key and under it stored non {@code null} value,
     * false - if nothing stored under given key or {@link #payload} doesn't have given key.
     * @throws IllegalArgumentException if key is {@code null} or empty.
     */
    public boolean hasPayload(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (this.payload.containsKey(key)) {
            return Objects.nonNull(this.payload.get(key));
        } else {
            return false;
        }
    }
}
