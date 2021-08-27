package io.kyberorg.yalsee.test.utils.report;

/**
 * Test execution result.
 *
 * @since 3.2.1
 */
public enum TestResult {
    /**
     * Test run as expected.
     */
    PASSED,

    /**
     * Test run without errors, but real stuff differs from excepted.
     */
    FAILED,

    /**
     * Test was ignored due to unmet pre-condition.
     */
    IGNORED,

    /**
     * Test was cancelled during its execution.
     */
    ABORTED
}
