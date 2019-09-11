package ee.yals.test.selenide.debug;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import ee.yals.test.utils.Selenide;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.LONG_URL_INPUT;
import static ee.yals.test.utils.selectors.FrontSelectors.MainRow.SUBMIT_BUTTON;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

public class Core {

    private static BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withDesiredCapabilities(DesiredCapabilities.chrome())
                    .withRecordingMode(RECORD_ALL, new File("target"));
    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(Selenide.Props.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    private final static String BASE_URL = System.getProperty(Selenide.Props.TEST_URL, LOCAL_URL);

    public static void setUp() {
        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        WebDriverRunner.setWebDriver(driver);
        Configuration.baseUrl = BASE_URL;
        //expose ports if testing local URL
        if (BASE_URL.equals(LOCAL_URL)) {
            Testcontainers.exposeHostPorts(SERVER_PORT);
        }
        //debug information
        System.out.println("");
        System.out.println("=== Debug Info ===");
        System.out.println("");
        System.out.println(String.format("Application will start at %d", SERVER_PORT));
        System.out.println(String.format("Will test BASE_URL: %s", BASE_URL));
        System.out.println("");
    }

    public static void clickIt() {
        $("#shortenIt").click();
    }

    static void pasteValueInFormAndSubmitIt(String link) {
        LONG_URL_INPUT.setValue(link);
        SUBMIT_BUTTON.click();
    }

    public static void tearDown() {
        chrome.stop();
    }
}
