package ee.yals.test.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.ScreenShooter;
import ee.yals.test.utils.Selenide;
import ee.yals.test.utils.TestUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;

import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.LONG_URL_INPUT;
import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.SUBMIT_BUTTON;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

/**
 * Common stuff for all UI Test
 *
 * @since 1.0
 */
public class UITest {
    private static String REPORT_DIRECTORY = System.getProperty(Selenide.Props.REPORT_DIR, Selenide.Defaults.REPORT_DIR);

    @ClassRule
    private static BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withCapabilities(new ChromeOptions())
                    .withRecordingMode(RECORD_ALL, new File(REPORT_DIRECTORY));

    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(Selenide.Props.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    public final static String BASE_URL = System.getProperty(Selenide.Props.TEST_URL, LOCAL_URL);

    @Rule
    public ScreenShooter makeScreenshotOnFailure = ScreenShooter.failedTests();

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

    public static void pasteValueInFormAndSubmitIt(String link) {
        LONG_URL_INPUT.setValue(link);
        SUBMIT_BUTTON.click();
    }

    public static boolean isBrowserHtmlUnit() {
        return TestUtils.whichBrowser().equals(Selenide.Browser.HTMLUNIT);
    }

    public static void tearDown() {
        //chrome.stop();
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
