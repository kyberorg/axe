package io.kyberorg.yalsee.test.utils;

import io.kyberorg.yalsee.test.TestApp;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * JUnit's 4 {@link TestWatcher} replacement.
 *
 * @since 2.7.6
 */
public class TestWatcherExtension implements TestWatcher, BeforeTestExecutionCallback {

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);


    private String testName;

    private long testStartTime;
    private float testDurationInMillis;
    private boolean testSucceeded;

    /**
     * Very first stage of running test. We use it for getting test name and logging executing startup.
     * @param extensionContext JUnit's test {@link ExtensionContext}
     */
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        testName = setTestNameFromContext(extensionContext);
        testStartTime = System.currentTimeMillis();
        System.out.printf("Starting.... build '%s'. Test: '%s%n", BUILD_NAME, testName);
    }

    /**
     * Marks test as succeeded.
     * @param context JUnit's test {@link ExtensionContext}
     */
    @Override
    public void testSuccessful(ExtensionContext context) {
        testDurationInMillis = System.currentTimeMillis() - testStartTime;
        testSucceeded = true;
        afterTest();
    }

    /**
     * Marks test as failed.
     * @param context JUnit's test {@link ExtensionContext}
     * @param cause exception led to test failure
     */
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        testDurationInMillis = System.currentTimeMillis() - testStartTime;
        testSucceeded = false;
        afterTest();
    }

    /**
     * Very last step of test execution.
     */
    private void afterTest() {
        String testResult = testSucceeded ? "OK" : "FAIL";
        String timeTook = testDurationInMillis/1000 +" s";

        System.out.printf("Finished(%s) build '%s'. Test: '%s, Time elapsed: %s%n",
                testResult,
                BUILD_NAME,
                testName,
                timeTook);
    }

    private String setTestNameFromContext(final ExtensionContext context) {
        String testClassName = context.getRequiredTestClass().getSimpleName();
        String rawMethodName = context.getRequiredTestMethod().getName();
        String[] methodAndBrowserInfo = rawMethodName.split("\\[");
        if (methodAndBrowserInfo.length > 0) {
            String method = methodAndBrowserInfo[0];
            return String.format("%s.%s", testClassName, method);
        } else {
            return String.format("%s.%s", testClassName, rawMethodName);
        }
    }
}
