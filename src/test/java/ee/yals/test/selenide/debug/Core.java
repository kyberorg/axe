package ee.yals.test.selenide.debug;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
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

    public static void setUp() {
        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        WebDriverRunner.setWebDriver(driver);

        Configuration.baseUrl = "https://dev.yals.eu";
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
