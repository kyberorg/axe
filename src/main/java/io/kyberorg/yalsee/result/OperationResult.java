package io.kyberorg.yalsee.result;

import com.google.gson.internal.Primitives;
import lombok.Getter;

/**
 * Class that defines result of operation. Operation can be any action: validation, query from database and so on.
 *
 * @since 3.0.4
 */
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

    @Getter private String result;
    @Getter private String message;
    private Object payload;

    /**
     * Default constructor.
     */
    protected OperationResult() { }

    /**
     * Create object with {@link #GENERAL_FAIL} status.
     *
     * @return new object.
     */
    public static OperationResult generalFail() {
        OperationResult result = new OperationResult();
        result.result = GENERAL_FAIL;
        return result;
    }

    /**
     * Create object with {@link #MALFORMED_INPUT} status.
     *
     * @return new object.
     */
    public static OperationResult malformedInput() {
        OperationResult result = new OperationResult();
        result.result = MALFORMED_INPUT;
        return result;
    }

    /**
     * Create object with {@link #OK} status.
     *
     * @return new object.
     */
    public static OperationResult success() {
        OperationResult result = new OperationResult();
        result.result = OK;
        return result;
    }

    /**
     * Create object with {@link #SYSTEM_DOWN} status and 'Database is DOWN' message.
     *
     * @return new object.
     */
    public static OperationResult databaseDown() {
        OperationResult result = new OperationResult();
        result.result = SYSTEM_DOWN;
        result.message = "Database is DOWN";
        return result;
    }

    /**
     * Create object with {@link #ELEMENT_NOT_FOUND} status.
     *
     * @return new object.
     */
    public static OperationResult elementNotFound() {
        OperationResult result = new OperationResult();
        result.result = ELEMENT_NOT_FOUND;
        return result;
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
    public OperationResult addPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public String getStringPayload() {
        return getPayload(String.class);
    }

    private <T> T getPayload(final Class<T> classOfPayload) {
        return Primitives.wrap(classOfPayload).cast(this.payload);
    }
}
