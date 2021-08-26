package io.kyberorg.yalsee.test;

import com.codeborne.selenide.Configuration;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestWatcherExtension;
import io.kyberorg.yalsee.test.utils.report.Test;
import io.kyberorg.yalsee.test.utils.report.TestReport;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Locale;

/**
 * Global methods.
 *
 * @since 3.2.1
 */
@ExtendWith(TestWatcherExtension.class) // catching test results and logging results to System.out
public abstract class YalseeTest {

    protected static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);
    protected static final String BASE_URL = TestUtils.getTestUrl();
    protected static final String REPORT_DIRECTORY =
            System.getProperty(TestApp.Properties.REPORT_DIR, TestApp.Defaults.Selenide.REPORT_DIR);

    private static final boolean REPORT_PASSED_TESTS =
            Boolean.parseBoolean(
                    System.getProperty(TestApp.Properties.REPORT_PASSED_TESTS, TestApp.Defaults.REPORT_PASSED_TESTS));

    /**
     * Global init (before all tests).
     */
    @BeforeAll
    public static void init() {
        if (Mutex.getInstance().isInitExecuted()) return;
        defineRunMode();
        SelenideTest.initSelenide();
        displayCommonInfo();
        registerShutdownHook(YalseeTest::afterAllTests);
        Mutex.getInstance().markInitAsExecuted();
    }

    /**
     * After all tests.
     */
    public static void afterAllTests() {
        if (Mutex.getInstance().isAfterTestsExecuted()) return;
        System.out.println("Testing is completed");
        printSummary();
        Mutex.getInstance().markAfterTestsAsExecuted();
    }

    /**
     * Are we running remotely (i.e. Grid/Selenoid etc.) ?
     *
     * @return true - if we are running tests at remotely, false if not.
     */
    protected boolean isRemoteRun() {
        return shouldRunTestsAtGrid();
    }

    protected static boolean shouldRunTestsAtGrid() {
        String selenideRemote = System.getProperty(TestApp.Properties.Selenide.REMOTE, "");
        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME, "");
        return StringUtils.isNotBlank(selenideRemote) || StringUtils.isNotBlank(gridHostname);
    }

    private static void defineRunMode() {
        if (shouldRunTestsAtGrid()) {
            //will run tests at Grid
            System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.GRID.name());
        } else {
            //application runs in docker container
            System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name());
        }
    }

    private static void displayCommonInfo() {
        TestApp.RunMode runMode = TestApp.RunMode.valueOf(
                System.getProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name())
        );
        String testLocation =
                runMode == TestApp.RunMode.GRID ? "at Grid (" + Configuration.remote + ")" : "locally";

        StringBuilder commonInfoBuilder = new StringBuilder(App.NEW_LINE);
        commonInfoBuilder.append("=== Tests Common Info ===").append(App.NEW_LINE);
        commonInfoBuilder.append(String.format("BuildName: %s", BUILD_NAME)).append(App.NEW_LINE);
        commonInfoBuilder.append(String.format("Will test %s", testLocation)).append(App.NEW_LINE);
        commonInfoBuilder.append(String.format("Test URL: %s", BASE_URL)).append(App.NEW_LINE);

        if (runMode == TestApp.RunMode.GRID) {
            commonInfoBuilder.append("Live Sessions: https://grid.kyberorg.io/#/").append(App.NEW_LINE);
            commonInfoBuilder.append(String.format("TestVideo: https://grid.kyberorg.io/video/%s.mp4", BUILD_NAME))
                    .append(App.NEW_LINE);
        } else {
            commonInfoBuilder.append(String.format("Videos and screenshots directory: %s", REPORT_DIRECTORY))
                    .append(App.NEW_LINE);
        }
        commonInfoBuilder.append("==================").append(App.NEW_LINE);

        System.out.println(commonInfoBuilder);
    }

    private static void registerShutdownHook(final Runnable method) {
        Runtime.getRuntime().addShutdownHook(new Thread(method));
    }

    private static void printSummary() {
        TestReport testReport = TestReport.getInstance();
        System.out.println("here is tests summary...");
        StringBuilder summary = new StringBuilder("===== Tests Summary =====");
        summary.append(App.NEW_LINE);

        summary.append("----- ").append(testReport.countFailedTests()).append(" tests failed").append(" -----");
        summary.append(App.NEW_LINE);
        for (Test test : testReport.getFailedTests()) {
            summary.append(test.getTestSuite()).append(".").append(test);
            if (test.getFailCause() != null) {
                summary.append(" Reason: ").append(test.getFailCause().getMessage());
            }
            summary.append(App.NEW_LINE);
        }
        summary.append("----- ").append(testReport.countPassedTests()).append(" tests passed").append(" -----");
        summary.append(App.NEW_LINE);
        if (REPORT_PASSED_TESTS) {
            for (Test test : testReport.getPassedTests()) {
                summary.append(test.getTestSuite()).append(".").append(test);
                summary.append(App.NEW_LINE);
            }
        } else {
            summary.append("Passed tests are not included. Please use -D")
                    .append(TestApp.Properties.REPORT_PASSED_TESTS).append("=true to include them.");
            summary.append(App.NEW_LINE);
        }

        summary.append("----- ").append(testReport.countIgnoredTests()).append(" tests ignored").append(" -----");
        summary.append(App.NEW_LINE);
        for (Test test : testReport.getIgnoredTests()) {
            summary.append(test.getTestSuite()).append(".").append(test);
            if (StringUtils.isNotBlank(test.getIgnoreReason())) {
                summary.append(" Reason: ").append(test.getIgnoreReason());
            }
            summary.append(App.NEW_LINE);
        }

        summary.append("----- ").append(testReport.countAbortedTests()).append(" tests aborted").append(" -----");
        summary.append(App.NEW_LINE);
        for (Test test : testReport.getAbortedTests()) {
            summary.append(test.getTestSuite()).append(".").append(test);
            if (test.getAbortedCause() != null) {
                summary.append(" Reason: ").append(test.getAbortedCause().getMessage());
            }
            summary.append(App.NEW_LINE);
        }

        String totalTimeSpent = testReport.getTotalTimeSpent().toString().substring(2).toLowerCase(Locale.ROOT);
        summary.append("Total tests run: ").append(testReport.countCompletedTests())
                .append(" from ").append(testReport.countSuites()).append(" suites completed in ")
                .append(totalTimeSpent);

        summary.append(String.valueOf(App.NEW_LINE).repeat(3));
        System.out.println(summary);
    }

    private static class Mutex {
        private static Mutex instance = null;
        @Getter
        private boolean initExecuted = false;
        @Getter
        private boolean afterTestsExecuted = false;

        public static synchronized Mutex getInstance() {
            if (instance == null) {
                instance = new Mutex();
            }
            return instance;
        }

        public void markInitAsExecuted() {
            this.initExecuted = true;
        }

        public void markAfterTestsAsExecuted() {
            this.afterTestsExecuted = true;
        }
    }
}
