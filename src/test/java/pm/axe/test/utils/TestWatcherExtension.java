package pm.axe.test.utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import pm.axe.test.AxeTest;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.report.TestData;
import pm.axe.test.utils.report.TestReport;
import pm.axe.test.utils.report.TestResult;
import pm.axe.test.utils.report.TestSuite;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.*;

import java.util.List;
import java.util.Optional;

/**
 * JUnit's 4 {@link TestWatcher} replacement.
 *
 * @since 2.7.6
 */
public class TestWatcherExtension implements TestWatcher, BeforeTestExecutionCallback, AfterTestExecutionCallback,
        TestExecutionExceptionHandler, LifecycleMethodExecutionExceptionHandler {

    private TestSuite testSuite;
    private TestData testData;

    private long testStartTime;
    private long testDurationInMillis;

    /**
     * Very first stage of running test. We use it for getting test start time and displaying test name (if applicable).
     *
     * @param context JUnit's test {@link ExtensionContext}
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        testStartTime = System.currentTimeMillis();
        if (AxeTest.SHOW_TEST_NAMES_IN_VIDEO && isUITest(context)) {
            String testName = getTestName(context);
            Selenide.executeJavaScript("if (typeof showTestName === \"function\") {"
                    + "showTestName(\"" + testName + "\") "
                    + "}"
            );
            Selenide.$("#testName").shouldBe(Condition.visible);
        }
    }

    /**
     * Stage that executed after test run. We use it for timing.
     *
     * @param context JUnit's test {@link ExtensionContext}
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) {
        testDurationInMillis = System.currentTimeMillis() - testStartTime;
        if (AxeTest.SHOW_TEST_NAMES_IN_VIDEO && isUITest(context)) {
            //clean test name
            Selenide.executeJavaScript("if (typeof removeTestName === \"function\") {"
                    + "removeTestName()"
                    + "}"
            );
        }
    }

    /**
     * Marks test as succeeded.
     *
     * @param context JUnit's test {@link ExtensionContext}
     */
    @Override
    public void testSuccessful(final ExtensionContext context) {
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
        testSuite = TestSuite.create(context.getRequiredTestClass());
        testData = TestData.create(setTestNameFromContext(context));

        testData.setTestResult(TestResult.ABORTED);
        testData.setAbortedCause(cause);

        afterTest();
    }

    @Override
    public void handleTestExecutionException(final ExtensionContext context, final Throwable throwable)
            throws Throwable {
        testSuite = TestSuite.create(context.getRequiredTestClass());
        testData = TestData.create(setTestNameFromContext(context));

        testData.setTestResult(TestResult.ON_FIRE);
        testData.setOnFireReason(throwable);

        afterTest();
        throw throwable;
    }

    @Override
    public void handleBeforeAllMethodExecutionException(final ExtensionContext context, final Throwable throwable)
            throws Throwable {
        List<String> testNames = TestUtils.getAllTestNames(context.getRequiredTestClass());

        testSuite = TestSuite.create(context.getRequiredTestClass());
        for (String testName : testNames) {
            testData = TestData.create(testName);
            testData.setTestResult(TestResult.ON_FIRE);
            testData.setOnFireReason(new RuntimeException("Exception at @BeforeAll method"));

            afterTest();
        }

        LifecycleMethodExecutionExceptionHandler.super.handleBeforeAllMethodExecutionException(context, throwable);
    }

    @Override
    public void handleBeforeEachMethodExecutionException(final ExtensionContext context, final Throwable throwable)
            throws Throwable {
        List<String> testNames = TestUtils.getAllTestNames(context.getRequiredTestClass());

        testSuite = TestSuite.create(context.getRequiredTestClass());
        for (String testName : testNames) {
            testData = TestData.create(testName);
            testData.setTestResult(TestResult.ON_FIRE);
            testData.setOnFireReason(new RuntimeException("Exception at @Before method"));

            afterTest();
        }

        LifecycleMethodExecutionExceptionHandler.super.handleBeforeEachMethodExecutionException(context, throwable);
    }

    @Override
    public void handleAfterEachMethodExecutionException(final ExtensionContext context, final Throwable throwable)
            throws Throwable {
        List<String> testNames = TestUtils.getAllTestNames(context.getRequiredTestClass());

        testSuite = TestSuite.create(context.getRequiredTestClass());
        for (String testName : testNames) {
            testData = TestData.create(testName);
            testData.setTestResult(TestResult.ON_FIRE);
            testData.setOnFireReason(new RuntimeException("Exception at @AfterEach method"));

            afterTest();
        }
        LifecycleMethodExecutionExceptionHandler.super.handleAfterEachMethodExecutionException(context, throwable);
    }

    @Override
    public void handleAfterAllMethodExecutionException(final ExtensionContext context, final Throwable throwable)
            throws Throwable {
        TestReport.getReport().markSuiteAsBroken();
        LifecycleMethodExecutionExceptionHandler.super.handleAfterAllMethodExecutionException(context, throwable);
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
        if (report.countOnFireTests() > 0) {
            sb.append("On Fire: ").append(report.countOnFireTests()).append(", ");
        }
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

    private boolean isUITest(final ExtensionContext context) {
        final Optional<Class<?>> testClass = context.getTestClass();
        return testClass.map(SelenideTest.class::isAssignableFrom).orElse(false);
    }

    private String getTestName(final ExtensionContext context) {
        TestSuite suite = TestSuite.create(context.getRequiredTestClass());
        TestData testData = TestData.create(setTestNameFromContext(context));
        testData.setTestSuite(suite);
        return testData.toString();
    }
}
