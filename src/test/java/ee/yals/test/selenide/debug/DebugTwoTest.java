package ee.yals.test.selenide.debug;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

public class DebugTwoTest {
    private static BrowserWebDriverContainer chrome =
            new BrowserWebDriverContainer()
                    .withDesiredCapabilities(DesiredCapabilities.chrome())
                    .withRecordingMode(RECORD_ALL, new File("target"));

    @BeforeClass
    public static void setUp() {
        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        WebDriverRunner.setWebDriver(driver);

        Configuration.baseUrl = "https://dev.yals.eu";
    }

    @Test
    public void testFullUrl() throws InterruptedException {
        Selenide.open("https://dev.yals.eu");
        $("#longUrl").setValue("https://ci.yadev.eu/job/YalsGH/job/boot2/");
        Core.clickIt();
    }

    @Test
    public void testAbsolute() throws InterruptedException {
        Selenide.open("/");
        $("#longUrl").setValue("https://ci.yadev.eu/job/YalsGH/");
        Core.clickIt();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        chrome.stop();
    }
}
