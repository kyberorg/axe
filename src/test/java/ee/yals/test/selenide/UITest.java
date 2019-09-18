package ee.yals.test.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.ScreenShooter;
import ee.yals.test.utils.Selenide;
import ee.yals.test.utils.TestUtils;
import ee.yals.test.utils.YalsTestDescription;
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

import static ee.yals.test.utils.pages.FrontPage.MainRow.LONG_URL_INPUT;
import static ee.yals.test.utils.pages.FrontPage.MainRow.SUBMIT_BUTTON;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;

/**
 * Common stuff for all UI Test
 *
 * @since 1.0
 */
@Slf4j
public class UITest {
    private static String REPORT_DIRECTORY = System.getProperty(Selenide.Props.REPORT_DIR, Selenide.Defaults.REPORT_DIR);
    private static BrowserWebDriverContainer.VncRecordingMode TESTS_RECORDING_MODE = RECORD_FAILING;

    private static BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withRecordingMode(TESTS_RECORDING_MODE, new File(REPORT_DIRECTORY))
                    .withCapabilities(new ChromeOptions());

    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(Selenide.Props.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    protected final static String BASE_URL = System.getProperty(Selenide.Props.TEST_URL, LOCAL_URL);

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
        LONG_URL_INPUT.setValue(link);
        SUBMIT_BUTTON.click();
    }

    protected static boolean isBrowserHtmlUnit() {
        return TestUtils.whichBrowser().equals(Selenide.Browser.HTMLUNIT);
    }

    @AfterClass
    public static void tearDown() {
        //actions after all tests
    }

    private static void debugInfo() {
        System.out.println("");
        System.out.println("=== Debug Info ===");
        System.out.println("");
        System.out.println(String.format("Will test BASE_URL: %s", BASE_URL));
        System.out.println(String.format("Application will start at %d", SERVER_PORT));
        System.out.println(String.format("Videos and screenshots directory: %s", REPORT_DIRECTORY));
        System.out.println("");
        System.out.println("==================");
        System.out.println("");
    }
}
