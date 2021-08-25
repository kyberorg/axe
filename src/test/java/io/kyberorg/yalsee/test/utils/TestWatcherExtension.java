package io.kyberorg.yalsee.test.utils;

import io.kyberorg.yalsee.test.TestApp;
import io.kyberorg.yalsee.test.utils.report.Test;
import io.kyberorg.yalsee.test.utils.report.TestReport;
import io.kyberorg.yalsee.test.utils.report.TestResult;
import io.kyberorg.yalsee.test.utils.report.TestSuite;
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

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);
    private static final int MILLISECONDS_IN_SECOND = 1000;

    private TestSuite testSuite;
    private Test test;

    private long testStartTime;
    private float testDurationInMillis;

    /**
     * Very first stage of running test. We use it for getting test name and logging executing startup.
     *
     * @param extensionContext JUnit's test {@link ExtensionContext}
     */
    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) {
        testSuite = TestSuite.create(extensionContext.getRequiredTestClass());
        test = Test.create(setTestNameFromContext(extensionContext));
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
        test.setTestResult(TestResult.FAILED);
        afterTest();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        testDurationInMillis = 0;
        test.setTestResult(TestResult.IGNORED);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        testDurationInMillis = 0;
        test.setTestResult(TestResult.ABORTED);
    }

    /**
     * Very last step of test execution.
     */
    private void afterTest() {
        String timeTook = testDurationInMillis / MILLISECONDS_IN_SECOND + " s";
        test.setTimeTook(timeTook);

        TestReport.getInstance().reportTestFinished(testSuite, test);
        printReport();
    }

    private void printReport() {
        TestReport report = TestReport.getInstance();
        System.out.printf("Failed: %d, Passed: %d, Ignored: %d, Aborted: %d of %d suites. Total: %d\n",
                report.countFailedTests(),
                report.countPassedTests(),
                report.countIgnoredTests(),
                report.countAbortedTests(),
                report.countSuites(),
                report.countCompletedTests());
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
