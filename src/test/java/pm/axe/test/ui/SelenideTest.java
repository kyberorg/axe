package pm.axe.test.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.openqa.selenium.MutableCapabilities;
import pm.axe.test.AxeTest;
import pm.axe.test.TestApp;
import pm.axe.test.utils.TestUtils;

import static com.codeborne.selenide.Selenide.clipboard;

/**
 * Base for all UI Tests, which run with Selenide.
 *
 * @since 1.0
 */
@ExtendWith(ScreenShooterExtension.class) // automatically takes screenshot of every failed test
public abstract class SelenideTest extends AxeTest {
    private static final String REPORT_DIRECTORY =
            System.getProperty(TestApp.Properties.REPORT_DIR, TestApp.Defaults.Selenide.REPORT_DIR);
    private static final String SELENIDE_BROWSER =
            System.getProperty(TestApp.Properties.Selenide.BROWSER, TestApp.Defaults.Selenide.BROWSER);
    private static final long SELENIDE_TIMEOUT =
            Long.parseLong(System.getProperty(TestApp.Properties.Selenide.TIMEOUT, TestApp.Defaults.Selenide.TIMEOUT));

    protected static final String APP_SHORT_URL = TestUtils.getAppShortUrl();
    protected static final int EXTENDED_LOAD_TIMEOUT_SECONDS = 40;

    /**
     * Selenide Setup Runs once per application by {@link AxeTest#init()}.
     */
    public static void initSelenide() {
        Configuration.baseUrl = BASE_URL;
        Configuration.reportsFolder = REPORT_DIRECTORY;
        Configuration.timeout = SELENIDE_TIMEOUT;
        Configuration.browser = SELENIDE_BROWSER;
        Configuration.browserSize = "1920x1080";
        //critical for Vaadin input
        Configuration.fastSetValue = true;

        if (shouldRunTestsAtGrid()) {
            Configuration.remote = getGridFullUrl();
            tuneDriverWithCapabilities();
        }
    }

    /**
     * Cleans Clipboard (local or remote), if it is not empty.
     */
    protected static void cleanClipboard() {
        if (StringUtils.isNotBlank(clipboard().getText())) {
            clipboard().setText("");
        }
    }

    private static void tuneDriverWithCapabilities() {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("enableVnc", true);
        capabilities.setCapability("screenResolution", "1920x1080x24");

        capabilities.setCapability("name", BUILD_NAME);

        capabilities.setCapability("enableVideo", true);
        capabilities.setCapability("videoName", BUILD_NAME + ".mp4");

        capabilities.setCapability("enableLog", true);
        capabilities.setCapability("logName", BUILD_NAME + ".log");

        Configuration.browserCapabilities = capabilities;
        System.out.println("Running with following" + Configuration.browserCapabilities);
    }

    private static String getGridFullUrl() {
        final String httpsPrefix = "https://";
        final String httpPrefix = "http://";
        final String gridPostfix = "/wd/hub";

        String selenideRemote = System.getProperty(TestApp.Properties.Selenide.REMOTE, "");
        if (StringUtils.isNotBlank(selenideRemote)) {
            return selenideRemote;
        }

        String gridHostname = System.getProperty(TestApp.Properties.GRID_HOSTNAME);
        boolean hostnameStringHasProtocol = gridHostname.startsWith(httpsPrefix) || gridHostname.startsWith(httpPrefix);
        boolean hostnameStringHasGridPostfix = gridHostname.contains(gridPostfix);
        if (hostnameStringHasProtocol && hostnameStringHasGridPostfix) {
            return gridHostname;
        } else if (hostnameStringHasProtocol) {
            return gridHostname + gridPostfix;
        } else if (hostnameStringHasGridPostfix) {
            return httpPrefix + gridHostname;
        } else {
            return httpPrefix + gridHostname + gridPostfix;
        }
    }
}
