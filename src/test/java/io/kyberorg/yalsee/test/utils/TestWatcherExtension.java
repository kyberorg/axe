package io.kyberorg.yalsee.test.utils;

import io.kyberorg.yalsee.test.utils.report.Test;
import io.kyberorg.yalsee.test.utils.report.TestReport;
import io.kyberorg.yalsee.test.utils.report.TestResult;
import io.kyberorg.yalsee.test.utils.report.TestSuite;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * JUnit's 4 {@link TestWatcher} replacement.
 *
 * @since 2.7.6
 */
public class TestWatcherExtension implements TestWatcher, BeforeTestExecutionCallback {

    private TestSuite testSuite;
    private Test test;

    private long testStartTime;
    private long testDurationInMillis;

    /**
     * Very first stage of running test. We use it for getting test name and logging executing startup.
     *
     * @param extensionContext JUnit's test {@link ExtensionContext}
     */
    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) {
        testStartTime = System.currentTimeMillis();
    }

    /**
     * Marks test as succeeded.
     *
     * @param context JUnit's test {@link ExtensionContext}
     */
    @Override
    public void testSuccessful(final ExtensionContext context) {
        testDurationInMillis = System.currentTimeMillis() - testStartTime;

        testSuite = TestSuite.create(context.getRequiredTestClass());
        test = Test.create(setTestNameFromContext(context));

        test.setTestResult(TestResult.PASSED);

        afterTest();
    }

    /**
     * Marks test as failed.
     *
     * @param context JUnit's test {@link ExtensionContext}
     * @param cause exception led to test failure
     */
    @Override
    public void testFailed(final ExtensionContext context, final Throwable cause) {
        testDurationInMillis = System.currentTimeMillis() - testStartTime;

        testSuite = TestSuite.create(context.getRequiredTestClass());
        test = Test.create(setTestNameFromContext(context));

        test.setTestResult(TestResult.FAILED);
        test.setFailCause(cause);

        afterTest();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        testDurationInMillis = 0;

        testSuite = TestSuite.create(context.getRequiredTestClass());
        test = Test.create(setTestNameFromContext(context));

        test.setTestResult(TestResult.IGNORED);
        reason.ifPresent(s -> test.setIgnoreReason(s));

        afterTest();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        testDurationInMillis = 0;

        testSuite = TestSuite.create(context.getRequiredTestClass());
        test = Test.create(setTestNameFromContext(context));

        test.setTestResult(TestResult.ABORTED);
        test.setAbortedCause(cause);

        afterTest();
    }

    /**
     * Very last step of test execution.
     */
    private void afterTest() {
        test.setTimeTookMillis(testDurationInMillis);

        TestReport.getInstance().reportTestFinished(testSuite, test);
        printReport();
    }

    private void printReport() {
        TestReport report = TestReport.getInstance();

        StringBuilder sb = new StringBuilder();
        if (report.countFailedTests() > 0) {
            sb.append("Failed: ").append(report.countFailedTests()).append(", ");
        }
        if (report.countPassedTests() > 0) {
            sb.append("Passed: ").append(report.countPassedTests()).append(", ");
        }
        if (report.countIgnoredTests() > 0) {
            sb.append("Ignored: ").append(report.countIgnoredTests()).append(", ");
        }
        if (report.countAbortedTests() > 0) {
            sb.append("Aborted: ").append(report.countAbortedTests()).append(", ");
        }

        String stringWithoutTrailingComma = StringUtils.chop(sb.toString().trim());

        String reportString = stringWithoutTrailingComma + " from " + report.countSuites() + " suites. " +
                "Total: " + report.countCompletedTests() + " tests.";

        System.out.println(reportString);
    }

    private String setTestNameFromContext(final ExtensionContext context) {
        String rawMethodName = context.getRequiredTestMethod().getName();
        String[] methodAndBrowserInfo = rawMethodName.split("\\[");
        if (methodAndBrowserInfo.length > 0) {
            return methodAndBrowserInfo[0];
        } else {
            return rawMethodName;
        }
    }
}
