package eu.yals.test.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.ScreenShooter;
import eu.yals.constants.App;
import eu.yals.test.TestApp;
import eu.yals.test.utils.YalsTestDescription;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.platform.commons.util.StringUtils;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.util.Optional;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;

/**
 * Base for all UI Tests, which run with Selenide.
 *
 * @since 1.0
 */
public abstract class SelenideTest {
    private static final String REPORT_DIRECTORY = System.getProperty(TestApp.Properties.REPORT_DIR, TestApp.Defaults.Selenide.REPORT_DIR);
    private static final String SELENIDE_BROWSER = System.getProperty(TestApp.Properties.Selenide.BROWSER, TestApp.Defaults.Selenide.BROWSER);
    private static final long SELENIDE_TIMEOUT = Long.parseLong(System.getProperty(TestApp.Properties.Selenide.TIMEOUT, TestApp.Defaults.Selenide.TIMEOUT));

    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(TestApp.Properties.SERVER_PORT, TestApp.Defaults.SERVER_PORT));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    protected final static String BASE_URL = System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);

    private static final BrowserWebDriverContainer.VncRecordingMode TESTS_RECORDING_MODE = RECORD_FAILING;

    @SuppressWarnings("rawtypes")
    private static final BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withRecordingMode(TESTS_RECORDING_MODE, new File(REPORT_DIRECTORY))
                    .withCapabilities(new ChromeOptions());

    private static String testName;
    private static boolean isCommonInfoAlreadyShown;

    @Rule  // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();

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
            System.out.printf("Starting build '%s'. Test: '%s%n", BUILD_NAME, testName);
        }

        /**
         * Marks test as succeeded. We report result to Zalenium or to TestContainers
         * @param description JUnit's test {@link Description} from Runner
         */
        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            if (shouldRunTestsAtGrid()) {
                Cookie cookie = new Cookie("zaleniumTestPassed", "true");
                getWebDriver().manage().addCookie(cookie);
            } else {
                chrome.afterTest(YalsTestDescription.fromDescription(description), Optional.empty());
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
            if (shouldRunTestsAtGrid()) {
                Cookie cookie = new Cookie("zaleniumTestPassed", "false");
                getWebDriver().manage().addCookie(cookie);
            } else {
                chrome.afterTest(YalsTestDescription.fromDescription(description), Optional.of(e));
            }
        }

        /**
         * Very last step of test execution.
         * @param description JUnit's test {@link Description} from Runner
         */
        @Override
        protected void finished(Description description) {
            super.finished(description);
            System.out.printf("Finished build '%s'. Test: '%s%n", BUILD_NAME, testName);
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
            //addBuildNameToDriver();
            //will run tests at Grid
            System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.GRID.name());
        } else {
            //expose ports if testing local URL
            if (BASE_URL.equals(LOCAL_URL)) {
                Testcontainers.exposeHostPorts(SERVER_PORT);
            }
            chrome.start();
            RemoteWebDriver driver = chrome.getWebDriver();
            WebDriverRunner.setWebDriver(driver);

            //application runs in docker container
            System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.CONTAINER.name());
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
     * Hook, which updates TestName in Grid.
     * It uses Driver and therefore needs to run after {@link com.codeborne.selenide.Selenide#open()} method.
     */
    protected void updateTestNameAndStartVideo() {
        if (shouldRunTestsAtGrid()) {
            setTestName();
            startVideo();
        }
    }

    /**
     * Actions performed after test completes.
     */
    @After
    public void afterTest() {
        if (shouldRunTestsAtGrid()) {
            resetTestNameAfterTestCompleted();
            stopVideo();
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
            TestApp.RunMode runMode = TestApp.RunMode.valueOf(System.getProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.CONTAINER.name()));
            String testRunner = runMode == TestApp.RunMode.GRID ? "Grid" : "TestContainers";
            String commonInfo = "" + App.NEW_LINE +
                    "=== UI Tests Common Info ===" +
                    App.NEW_LINE +
                    String.format("BuildName: %s", BUILD_NAME) + App.NEW_LINE +
                    String.format("Will test BASE_URL: %s", BASE_URL) + App.NEW_LINE +
                    String.format("Will test at : %s", testRunner) + App.NEW_LINE +
                    String.format("Application will start at %d", SERVER_PORT) + App.NEW_LINE +
                    String.format("Videos and screenshots directory: %s", REPORT_DIRECTORY) + App.NEW_LINE +
                    String.format("Live Sessions: https://grid.yatech.eu/grid/admin/live?build=%s", BUILD_NAME) +
                    App.NEW_LINE +
                    "==================" +
                    App.NEW_LINE;
            System.out.println(commonInfo);
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

    private static void addBuildNameToDriver() {
        DesiredCapabilities extraCapabilities = new DesiredCapabilities();
        extraCapabilities.setCapability("build", BUILD_NAME);
        Configuration.browserCapabilities = extraCapabilities;
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

    private void setTestName() {
        Cookie cookie = new Cookie("zaleniumTestName", testName);
        getWebDriver().manage().addCookie(cookie);
    }

    private void resetTestNameAfterTestCompleted() {
        MutableCapabilities desiredCapabilities = Configuration.browserCapabilities;
        if (desiredCapabilities.is("name")) {
            desiredCapabilities.asMap().remove("name");
        }
        Configuration.browserCapabilities = desiredCapabilities;
    }

    private void startVideo() {
        Cookie videoCookie = new Cookie("zaleniumVideo", "true");
        getWebDriver().manage().addCookie(videoCookie);
    }

    private void stopVideo() {
        Cookie videoCookie = new Cookie("zaleniumVideo", "false");
        getWebDriver().manage().addCookie(videoCookie);
    }
}
