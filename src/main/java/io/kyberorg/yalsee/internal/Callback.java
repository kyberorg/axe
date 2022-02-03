package io.kyberorg.yalsee.internal;

/**
 * Special interface to accept lambdas as method parameters.
 *
 * @since 3.10
 */
@FunctionalInterface
public interface Callback {
    /**
     * Triggers execution.
     */
    void execute();
}
