package io.kyberorg.yalsee.test.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.test.TestApp;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.utils.TestWatcherExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.openqa.selenium.MutableCapabilities;


/**
 * Base for all UI Tests, which run with Selenide.
 *
 * @since 1.0
 */
@ExtendWith(ScreenShooterExtension.class) // automatically takes screenshot of every failed test
@ExtendWith(TestWatcherExtension.class) // catching test results and logging results to System.out
public abstract class SelenideTest {
    private static final String REPORT_DIRECTORY = System.getProperty(TestApp.Properties.REPORT_DIR, TestApp.Defaults.Selenide.REPORT_DIR);
    private static final String SELENIDE_BROWSER = System.getProperty(TestApp.Properties.Selenide.BROWSER, TestApp.Defaults.Selenide.BROWSER);
    private static final long SELENIDE_TIMEOUT = Long.parseLong(System.getProperty(TestApp.Properties.Selenide.TIMEOUT, TestApp.Defaults.Selenide.TIMEOUT));

    protected final static String BASE_URL = TestUtils.getTestUrl();

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);

    private static boolean isCommonInfoAlreadyShown;

    /**
     * Common Runner Setup and Info.
     */
    @BeforeAll
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
    @AfterAll
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

}
