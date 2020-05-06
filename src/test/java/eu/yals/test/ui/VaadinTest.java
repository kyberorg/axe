package eu.yals.test.ui;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.testbench.parallel.SauceLabsIntegration;
import com.vaadin.testbench.parallel.setup.SetupDriver;
import eu.yals.test.TestApp;
import eu.yals.test.TestUtils;
import eu.yals.test.utils.elements.VaadinElement;
import eu.yals.test.utils.elements.YalsElement;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.ProtocolHandshake;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for all Vaadin aka UI Tests.
 *
 * @since 2.7
 */
@Slf4j
public abstract class VaadinTest extends ParallelTest {
    static {
        Logger sauceLabsLog = Logger.getLogger(SauceLabsIntegration.class.getName());
        Logger selenideRemoteLog = Logger.getLogger(ProtocolHandshake.class.getName());

        sauceLabsLog.setLevel(Level.SEVERE);
        selenideRemoteLog.setLevel(Level.WARNING);
    }

    private static final int SERVER_PORT =
            Integer.parseInt(
                    System.getProperty(TestApp.Properties.SERVER_PORT, TestApp.Defaults.SERVER_PORT));
    private static final String LOCAL_URL =
            String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    private static final String REPORT_DIRECTORY =
            System.getProperty(TestApp.Properties.REPORT_DIR, TestApp.Defaults.REPORT_DIR);
    protected static final String BASE_URL =
            System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    private static final String BUILD_NAME =
            System.getProperty(TestApp.Properties.BUILD_NAME, TestApp.Defaults.BUILD_NAME);

    private static String testName;

    @Rule
    public final TestRule watchman =
            new TestWatcher() {
                @Override
                protected void starting(final Description description) {
                    super.starting(description);
                    testName = setTestNameFromTestDescription(description);
                    log.info(String.format("Starting build '%s'. Test: '%s", BUILD_NAME, testName));
                }

                @Override
                protected void succeeded(final Description description) {
                    super.succeeded(description);
                    Cookie cookie = new Cookie("zaleniumTestPassed", "true");
                    getDriver().manage().addCookie(cookie);
                }

                @Override
                protected void failed(final Throwable e, final Description description) {
                    Cookie cookie = new Cookie("zaleniumTestPassed", "false");
                    getDriver().manage().addCookie(cookie);
                }
            };

    /**
     * Performs actions needed for running tests.
     *
     * @throws Exception if error occurred
     */
    @Before
    public void setup() throws Exception {
        addTestNameToDriver();

        super.setup();
        Parameters.setScreenshotErrorDirectory(REPORT_DIRECTORY);
    }

    protected void open(final String relativeOrAbsoluteUrl) {
        final String PROTOCOL_MARKER = "://";
        boolean isUrlAbsolute = relativeOrAbsoluteUrl.contains(PROTOCOL_MARKER);
        String urlToOpen = isUrlAbsolute ? relativeOrAbsoluteUrl : BASE_URL + relativeOrAbsoluteUrl;
        log.info("Opening page: {}" + urlToOpen);
        getDriver().get(urlToOpen);
    }

    protected YalsElement $$(final String cssSelector) {
        return YalsElement.wrap(findElement(By.cssSelector(cssSelector)));
    }

    protected VaadinElement $$(final TestBenchElement element) {
        return VaadinElement.wrap(element);
    }

    protected void waitUntilSiteLoads(int timeoutInSeconds) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.tagName("body")), timeoutInSeconds);
    }

    /**
     * Browsers configuration aka browser launch params.
     *
     * @return list with configured browsers
     */
    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        List<TestApp.Browser> testBrowsers = TestUtils.getTestBrowsers();
        List<DesiredCapabilities> browsers = new ArrayList<>();

        if (testBrowsers.contains(TestApp.Browser.CHROME)) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("enable-automation");
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-extensions");
            options.addArguments("--dns-prefetch-disable");
            options.addArguments("--disable-gpu");
            DesiredCapabilities chrome = BrowserUtil.chrome();
            chrome.setCapability(ChromeOptions.CAPABILITY, options);

            browsers.add(chrome);
        }

        if (testBrowsers.contains(TestApp.Browser.FIREFOX)) {
            browsers.add(BrowserUtil.firefox());
        }
        if (testBrowsers.contains(TestApp.Browser.SAFARI)) {
            browsers.add(BrowserUtil.safari());
        }
        if (testBrowsers.contains(TestApp.Browser.IE)) {
            browsers.add(BrowserUtil.ie11());
        }
        if (testBrowsers.contains(TestApp.Browser.EDGE)) {
            browsers.add(BrowserUtil.edge());
        }

        return browsers;
    }

    @SuppressWarnings("rawtypes")
    private void addTestNameToDriver() throws IllegalAccessException, ClassCastException {
        Class parallelTest = getClass();
        do {
            if (parallelTest == null) break;
            parallelTest = parallelTest.getSuperclass();
        } while (!parallelTest.getSimpleName().equals("ParallelTest"));

        if (parallelTest == null) return;

        Field[] parallelTestFields = parallelTest.getDeclaredFields();

        Field driverConfigurationField = null;
        for (Field f : parallelTestFields) {
            if (f.getName().equals("driverConfiguration")) {
                driverConfigurationField = f;
            }
        }
        if (driverConfigurationField == null) return;
        driverConfigurationField.setAccessible(true);
        SetupDriver sd = (SetupDriver) driverConfigurationField.get(this);
        if (sd == null || sd.getDesiredCapabilities() == null) return;
        sd.getDesiredCapabilities().setCapability("name", testName);
        sd.getDesiredCapabilities().setCapability("build", BUILD_NAME);
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
}
