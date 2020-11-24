package eu.yals.test.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import eu.yals.constants.App;
import eu.yals.test.TestApp;
import eu.yals.test.TestUtils;
import eu.yals.test.utils.retry.RetryRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.platform.commons.util.StringUtils;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.MutableCapabilities;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Base for all UI Tests, which run with Selenide.
 *
 * @since 1.0
 */
public abstract class SelenideTest {
    private static final String REPORT_DIRECTORY = System.getProperty(TestApp.Properties.REPORT_DIR, TestApp.Defaults.Selenide.REPORT_DIR);
    private static final String SELENIDE_BROWSER = System.getProperty(TestApp.Properties.Selenide.BROWSER, TestApp.Defaults.Selenide.BROWSER);
    private static final long SELENIDE_TIMEOUT = Long.parseLong(System.getProperty(TestApp.Properties.Selenide.TIMEOUT, TestApp.Defaults.Selenide.TIMEOUT));

    protected final static String BASE_URL = TestUtils.getTestUrl();

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);

    private static String testName;
    private static boolean isCommonInfoAlreadyShown;

    private long testStartTime;
    private float testDurationInMillis;
    private boolean testSucceeded;

    @Rule  // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();

    @Rule //retries flaky tests. Sometimes helps.
    public RetryRule rule = new RetryRule();

    @Rule  // catching test result and triggering BrowserWebDriverContainer#afterTest() for saving test recordings
    public final TestRule watchman = new TestWatcher() {
        /**
         * Very first stage of running test. We use it for getting test name and logging executing startup.
         * @param description JUnit's test {@link Description} from Runner
         */
        @Override
        protected void starting(final Description description) {
            super.starting(description);
            testName = setTestNameFromTestDescription(description);
            testStartTime = System.currentTimeMillis();
            System.out.printf("Starting.... build '%s'. Test: '%s%n", BUILD_NAME, testName);
        }

        /**
         * Marks test as succeeded. We report result to Zalenium or to TestContainers
         * @param description JUnit's test {@link Description} from Runner
         */
        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            testDurationInMillis = System.currentTimeMillis() - testStartTime;
            testSucceeded = true;
            if (shouldRunTestsAtGrid()) {
                Cookie cookie = new Cookie("zaleniumTestPassed", "true");
                getWebDriver().manage().addCookie(cookie);
            }
        }

        /**
         * Marks test as failed. We report result to Zalenium or to TestContainers
         * @param e exception that occurred at exec time.
         * @param description JUnit's test {@link Description} from Runner
         */
        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
            testDurationInMillis = System.currentTimeMillis() - testStartTime;
            testSucceeded = false;
            if (shouldRunTestsAtGrid()) {
                Cookie cookie = new Cookie("zaleniumTestPassed", "false");
                getWebDriver().manage().addCookie(cookie);
            }
        }

        /**
         * Very last step of test execution.
         * @param description JUnit's test {@link Description} from Runner
         */
        @Override
        protected void finished(Description description) {
            super.finished(description);
            String testResult = testSucceeded ? "OK" : "FAIL";
            String timeTook = testDurationInMillis/1000 +" s";

            System.out.printf("Finished(%s) build '%s'. Test: '%s, Time elapsed: %s%n", testResult, BUILD_NAME, testName, timeTook);
        }
    };

    /**
     * Common Runner Setup and Info.
     */
    @BeforeClass
    public static void setUp() {
        Configuration.baseUrl = BASE_URL;
        Configuration.reportsFolder = REPORT_DIRECTORY;
        Configuration.timeout = SELENIDE_TIMEOUT;
        Configuration.browser = SELENIDE_BROWSER;
        Configuration.startMaximized = true;
        //critical for Vaadin input
        Configuration.fastSetValue = true;

        if (shouldRunTestsAtGrid()) {
            Configuration.remote = getGridFullUrl();
            //will run tests at Grid
            System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.GRID.name());
        } else {
            //application runs in docker container
            System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name());
        }
        //display common information
        displayCommonInfo();
    }

    /**
     * Setting capabilities for driver.
     * Needs to run before {@link com.codeborne.selenide.Selenide#open()} method.
     */
    protected void tuneDriverWithCapabilities() {
        if(shouldRunTestsAtGrid()) {
            MutableCapabilities capabilities = new MutableCapabilities();
            capabilities.setCapability("enableVnc", true);
            capabilities.setCapability("screenResolution","1920x1080x24");

            capabilities.setCapability("name", BUILD_NAME);

            capabilities.setCapability("enableVideo", true);
            capabilities.setCapability("videoName", BUILD_NAME+".mp4");

            capabilities.setCapability("enableLog", true);
            capabilities.setCapability("logName", BUILD_NAME+".log");

            Configuration.browserCapabilities.merge(capabilities);
        }
    }

    /**
     * Actions after all tests.
     */
    @AfterClass
    public static void tearDown() {
        System.out.println("Testing is Done");
    }

    private static void displayCommonInfo() {
        if (!isCommonInfoAlreadyShown) {
            TestApp.RunMode runMode = TestApp.RunMode.valueOf(System.getProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name()));
            String testLocation = runMode == TestApp.RunMode.GRID ? "at Grid ("+Configuration.remote+")" : "locally";

            StringBuilder commonInfoBuilder =  new StringBuilder(App.NEW_LINE);
            commonInfoBuilder.append("=== UI Tests Common Info ===").append(App.NEW_LINE);
            commonInfoBuilder.append(String.format("BuildName: %s", BUILD_NAME)).append(App.NEW_LINE);
            commonInfoBuilder.append(String.format("Will test %s", testLocation)).append(App.NEW_LINE);
            commonInfoBuilder.append(String.format("Test URL: %s", BASE_URL)).append(App.NEW_LINE);

            if (runMode == TestApp.RunMode.GRID) {
                commonInfoBuilder.append("Live Sessions: https://grid-ui.yadev.eu/#/").append(App.NEW_LINE);
                commonInfoBuilder.append(String.format("TestVideo: https://grid-ui.yadev.eu/video/%s.mp4", BUILD_NAME))
                        .append(App.NEW_LINE);
            } else {
                commonInfoBuilder.append(String.format("Videos and screenshots directory: %s", REPORT_DIRECTORY))
                        .append(App.NEW_LINE);
            }
            commonInfoBuilder.append("==================").append(App.NEW_LINE);

            System.out.println(commonInfoBuilder.toString());
            isCommonInfoAlreadyShown = true;
        }
    }

    private static boolean shouldRunTestsAtGrid() {
        String selenideRemote = System.getProperty(TestApp.Properties.Selenide.REMOTE, "");
        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME, "");
        return StringUtils.isNotBlank(selenideRemote) || StringUtils.isNotBlank(gridHostname);
    }

    private static String getGridFullUrl() {
        final String HTTPS_PREFIX = "https://";
        final String HTTP_PREFIX = "http://";
        final String GRID_POSTFIX = "/wd/hub";

        String selenideRemote = System.getProperty(TestApp.Properties.Selenide.REMOTE, "");
        if (StringUtils.isNotBlank(selenideRemote)) {
            return selenideRemote;
        }

        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME);
        boolean hostnameStringHasProtocol = gridHostname.startsWith(HTTPS_PREFIX) || gridHostname.startsWith(HTTP_PREFIX);
        boolean hostnameStringHasGridPostfix = gridHostname.contains(GRID_POSTFIX);
        if (hostnameStringHasProtocol && hostnameStringHasGridPostfix) {
            return gridHostname;
        } else if(hostnameStringHasProtocol) {
            return gridHostname + GRID_POSTFIX;
        } else if(hostnameStringHasGridPostfix) {
            return HTTP_PREFIX + gridHostname;
        } else {
            return HTTP_PREFIX + gridHostname + GRID_POSTFIX;
        }
    }

    private String setTestNameFromTestDescription(final Description description) {
        String testClassName = description.getTestClass().getSimpleName();
        String rawMethodName = description.getMethodName();
        String[] methodAndBrowserInfo = rawMethodName.split("\\[");
        if (methodAndBrowserInfo.length > 0) {
            String method = methodAndBrowserInfo[0];
            return String.format("%s.%s", testClassName, method);
        } else {
            return String.format("%s.%s", testClassName, rawMethodName);
        }
    }

}
