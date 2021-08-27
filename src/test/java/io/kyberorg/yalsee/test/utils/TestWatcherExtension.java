package io.kyberorg.yalsee.test.utils;

import io.kyberorg.yalsee.test.utils.report.TestData;
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
    private TestData testData;

    private long testStartTime;
    private long testDurationInMillis;

    /**
     * Very first stage of running test. We use it for getting test start time.
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
        testData = TestData.create(setTestNameFromContext(context));

        testData.setTestResult(TestResult.PASSED);

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
        testData = TestData.create(setTestNameFromContext(context));

        testData.setTestResult(TestResult.FAILED);
        testData.setFailCause(cause);

        afterTest();
    }

    /**
     * Marks test as ignored.
     *
     * @param context JUnit's test {@link ExtensionContext}
     * @param reason  why test was ignored
     */
    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        testDurationInMillis = 0;

        testSuite = TestSuite.create(context.getRequiredTestClass());
        testData = TestData.create(setTestNameFromContext(context));

        testData.setTestResult(TestResult.IGNORED);
        reason.ifPresent(s -> testData.setIgnoreReason(s));

        afterTest();
    }

    /**
     * Marks test as aborted.
     *
     * @param context JUnit's test {@link ExtensionContext}
     * @param cause   exception led to test been aborted.
     */
    @Override
    public void testAborted(final ExtensionContext context, final Throwable cause) {
        testDurationInMillis = 0;

        testSuite = TestSuite.create(context.getRequiredTestClass());
        testData = TestData.create(setTestNameFromContext(context));

        testData.setTestResult(TestResult.ABORTED);
        testData.setAbortedCause(cause);

        afterTest();
    }

    /**
     * Very last step of test execution.
     */
    private void afterTest() {
        testData.setTimeTookMillis(testDurationInMillis);

        TestReport.getReport().reportTestFinished(testSuite, testData);
        printReport();
    }

    private void printReport() {
        TestReport report = TestReport.getReport();

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

        String reportString = stringWithoutTrailingComma + " from " + report.countSuites() + " suites. "
                + "Total: " + report.countCompletedTests() + " tests.";

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
