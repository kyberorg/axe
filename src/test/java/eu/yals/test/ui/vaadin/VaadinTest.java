package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTest;
import eu.yals.test.TestApp;
import eu.yals.test.utils.Selenide;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;

public abstract class VaadinTest<E extends TestBenchElement> extends ParallelTest {
    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(TestApp.Properties.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    private static final String REPORT_DIRECTORY = System.getProperty(TestApp.Selenide.REPORT_DIR, Selenide.Defaults.REPORT_DIR);
    protected final static String BASE_URL = System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    protected List<DesiredCapabilities> browsers;
    private static String testName;

    @ClassRule
    public static TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getDisplayName());
            testName = description.getDisplayName();
        }
    };

    @Before
    public void setup() throws Exception {
        super.setup();
        Parameters.setScreenshotErrorDirectory(REPORT_DIRECTORY);
        getDriver().get(BASE_URL);
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        System.out.println("Setting browsers");
        browsers = new ArrayList<>();
        browsers.add(BrowserUtil.chrome());
        browsers.add(BrowserUtil.firefox());
        setTestName(testName);
        return browsers;
    }

    private void setTestName(String testName) {
        for (DesiredCapabilities browser : browsers) {
            browser.setCapability("testFileNameTemplate", "{testName}_{browser}_{testStatus}");
            browser.setCapability("name", testName);
        }
    }

    protected abstract E openView();
}
