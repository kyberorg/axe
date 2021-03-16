package io.kyberorg.yalsee.result;

public class OperationResult {
    public boolean ok() {
        return false;
    }

    public boolean notOk() {
        return !ok();
    }
}
