package io.kyberorg.yalsee.result;

import lombok.Getter;

public class OperationResult {
    public static final String OK = "OP_OK";
    public static final String MALFORMED_INPUT = "OP_WRONG_INPUT";
    public static final String GENERAL_FAIL = "OP_GENERAL_FAIL";
    public static final String ELEMENT_NOT_FOUND = "OP_NO_ELEM";
    public static final String SYSTEM_DOWN = "OP_SYSTEM_DOWN";

    @Getter private String result;
    @Getter private String message;

    protected OperationResult() { }

    public static OperationResult generalFail() {
        OperationResult result = new OperationResult();
        result.result = GENERAL_FAIL;
        return result;
    }

    public static OperationResult malformedInput() {
        OperationResult result = new OperationResult();
        result.result = MALFORMED_INPUT;
        return result;
    }

    public static OperationResult success() {
        OperationResult result = new OperationResult();
        result.result = OK;
        return result;
    }

    public static OperationResult databaseDown() {
        OperationResult result = new OperationResult();
        result.result = SYSTEM_DOWN;
        result.message = "Database is DOWN";
        return result;
    }

    public static OperationResult elementNotFound() {
        OperationResult result = new OperationResult();
        result.result = ELEMENT_NOT_FOUND;
        return result;
    }

    public boolean ok() {
        return result.equals(OK);
    }

    public boolean notOk() {
        return !ok();
    }

    public OperationResult withMessage(final String customMessage) {
        this.message = customMessage;
        return this;
    }
}
