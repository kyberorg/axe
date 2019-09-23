package eu.yals.test.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.ScreenShooter;
import eu.yals.constants.App;
import eu.yals.test.TestApp;
import eu.yals.test.TestUtils;
import eu.yals.test.ui.pageobjects.FrontPage;
import eu.yals.test.utils.Selenide;
import eu.yals.test.utils.YalsTestDescription;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.util.Optional;

import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;

/**
 * Common stuff for all UI Test
 *
 * @since 1.0
 */
@Slf4j
public class UITest {
    private static final String REPORT_DIRECTORY = System.getProperty(TestApp.Selenide.REPORT_DIR, Selenide.Defaults.REPORT_DIR);
    private static final long SELENIDE_TIMEOUT = Long.parseLong(System.getProperty(TestApp.Selenide.TIMEOUT, Selenide.Defaults.TIMEOUT));
    private static final BrowserWebDriverContainer.VncRecordingMode TESTS_RECORDING_MODE = RECORD_FAILING;

    private static final BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withRecordingMode(TESTS_RECORDING_MODE, new File(REPORT_DIRECTORY))
                    .withCapabilities(new ChromeOptions());

    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(TestApp.Properties.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    protected final static String BASE_URL = System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    @Rule  // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();

    @Rule  // catching test result and triggering BrowserWebDriverContainer#afterTest() for saving test recordings
    public final TestRule watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            chrome.afterTest(YalsTestDescription.fromDescription(description), Optional.empty());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);
            chrome.afterTest(YalsTestDescription.fromDescription(description), Optional.of(e));
        }
    };

    @BeforeClass
    public static void setUp() {
        Configuration.baseUrl = BASE_URL;
        Configuration.reportsFolder = REPORT_DIRECTORY;
        Configuration.timeout = SELENIDE_TIMEOUT;

        //expose ports if testing local URL
        if (BASE_URL.equals(LOCAL_URL)) {
            Testcontainers.exposeHostPorts(SERVER_PORT);
        }
        //debug information
        debugInfo();

        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        WebDriverRunner.setWebDriver(driver);
    }

    protected static void pasteValueInFormAndSubmitIt(String link) {
        FrontPage.MainRow.LONG_URL_INPUT.setValue(link);
        FrontPage.MainRow.SUBMIT_BUTTON.click();
    }

    protected static boolean isBrowserHtmlUnit() {
        return TestUtils.whichBrowser().equals(Selenide.Browser.HTMLUNIT);
    }

    @AfterClass
    public static void tearDown() {
        //actions after all tests
        log.info("Testing is Done");
    }

    private static void debugInfo() {
        String debugInfo = "" + App.NEW_LINE +
                "=== Debug Info ===" +
                App.NEW_LINE +
                String.format("Will test BASE_URL: %s", BASE_URL) +
                String.format("Application will start at %d", SERVER_PORT) +
                String.format("Videos and screenshots directory: %s", REPORT_DIRECTORY) +
                App.NEW_LINE +
                "==================" +
                App.NEW_LINE;
        System.out.println(debugInfo);
    }
}
