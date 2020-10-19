package eu.yals.test.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.ScreenShooter;
import eu.yals.constants.App;
import eu.yals.test.TestApp;
import eu.yals.test.utils.Selenide;
import eu.yals.test.utils.YalsTestDescription;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.platform.commons.util.StringUtils;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.util.Optional;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;

@Slf4j
public abstract class SelenideTest {
    private static final String REPORT_DIRECTORY = System.getProperty(TestApp.Selenide.REPORT_DIR, Selenide.Defaults.REPORT_DIR);
    private static final String SELENIDE_BROWSER = System.getProperty(TestApp.Selenide.BROWSER, Selenide.Defaults.BROWSER);
    private static final long SELENIDE_TIMEOUT = Long.parseLong(System.getProperty(TestApp.Selenide.TIMEOUT, Selenide.Defaults.TIMEOUT));
    private static final BrowserWebDriverContainer.VncRecordingMode TESTS_RECORDING_MODE = RECORD_FAILING;

    @SuppressWarnings("rawtypes")
    private static final BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withRecordingMode(TESTS_RECORDING_MODE, new File(REPORT_DIRECTORY))
                    .withCapabilities(new ChromeOptions());

    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(TestApp.Properties.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    protected final static String BASE_URL = System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);

    private static String testName;

    @Rule  // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();

    @Rule  // catching test result and triggering BrowserWebDriverContainer#afterTest() for saving test recordings
    public final TestRule watchman = new TestWatcher() {
        @Override
        protected void starting(final Description description) {
            super.starting(description);
            testName = setTestNameFromTestDescription(description);
            log.info(String.format("Starting build '%s'. Test: '%s", BUILD_NAME, testName));
        }

        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            Cookie cookie = new Cookie("zaleniumTestPassed", "true");
            getWebDriver().manage().addCookie(cookie);
            chrome.afterTest(YalsTestDescription.fromDescription(description), Optional.empty());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
            Cookie cookie = new Cookie("zaleniumTestPassed", "false");
            getWebDriver().manage().addCookie(cookie);
            chrome.afterTest(YalsTestDescription.fromDescription(description), Optional.of(e));
        }
    };

    @BeforeClass
    public static void setUp() {
        Configuration.baseUrl = BASE_URL;
        Configuration.reportsFolder = REPORT_DIRECTORY;
        Configuration.timeout = SELENIDE_TIMEOUT;
        Configuration.browser = SELENIDE_BROWSER;

        if (shouldRunTestsAtGrid()) {
            Configuration.remote = getGridFullUrl();

            //to distinguish test in Grid
            addTestNameToDriver();
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
        //debug information
        debugInfo();
    }

    @AfterClass
    public static void tearDown() {
        //actions after all tests
        log.info("Testing is Done");
    }

    private static void debugInfo() {
        TestApp.RunMode runMode = TestApp.RunMode.valueOf(System.getProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.CONTAINER.name()));
        String testRunner = runMode == TestApp.RunMode.GRID ? getGridFullUrl() : "TestContainers";
        String debugInfo = "" + App.NEW_LINE +
                "=== Debug Info ===" +
                App.NEW_LINE +
                String.format("Will test BASE_URL: %s", BASE_URL) + App.NEW_LINE +
                String.format("Will test at : %s", testRunner) + App.NEW_LINE +
                String.format("Application will start at %d", SERVER_PORT) + App.NEW_LINE +
                String.format("Videos and screenshots directory: %s", REPORT_DIRECTORY) +
                App.NEW_LINE +
                "==================" +
                App.NEW_LINE;
        System.out.println(debugInfo);
    }

    private static boolean shouldRunTestsAtGrid() {
        String selenideRemote = System.getProperty(TestApp.Selenide.REMOTE, "");
        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME, "");
        return StringUtils.isNotBlank(selenideRemote) || StringUtils.isNotBlank(gridHostname);
    }

    private static String getGridFullUrl() {
        final String HTTPS_PREFIX = "https://";
        final String HTTP_PREFIX = "http://";
        final String GRID_POSTFIX = "/wd/hub";

        String selenideRemote = System.getProperty(TestApp.Selenide.REMOTE, "");
        if (StringUtils.isNotBlank(selenideRemote)) {
            return selenideRemote;
        }

        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME);
        boolean hostnameStringHasProtocol = gridHostname.startsWith(HTTPS_PREFIX) || gridHostname.startsWith(HTTP_PREFIX);
        boolean hostnameStringHasGridPostfix = gridHostname.contains(GRID_POSTFIX);
        if (hostnameStringHasProtocol && hostnameStringHasGridPostfix) {
            return gridHostname;
        } else {
            return HTTPS_PREFIX + gridHostname + GRID_POSTFIX;
        }
    }

    private String setTestNameFromTestDescription(final Description description) {
        String rawMethodName = description.getMethodName();
        String[] methodAndBrowserInfo = rawMethodName.split("\\[");
        if (methodAndBrowserInfo.length > 0) {
            String method = methodAndBrowserInfo[0];
            return String.format("%s.%s", description.getTestClass().getName(), method);
        } else {
            return String.format("%s.%s", description.getTestClass().getName(), rawMethodName);
        }
    }

    private static void addTestNameToDriver() {
        DesiredCapabilities extraCapabilities = new DesiredCapabilities();
        System.out.println("Test Name: " + testName);
        extraCapabilities.setCapability("name", "MySuperName");
        extraCapabilities.setCapability("build", BUILD_NAME);
        Configuration.browserCapabilities = extraCapabilities;
    }
}
