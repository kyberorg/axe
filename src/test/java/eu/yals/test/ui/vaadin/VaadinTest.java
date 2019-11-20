package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.testbench.parallel.setup.SetupDriver;
import eu.yals.test.TestApp;
import eu.yals.test.utils.Selenide;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Cookie;

import java.lang.reflect.Field;

@Slf4j
public abstract class VaadinTest<E extends TestBenchElement> extends ParallelTest {
    private final static int SERVER_PORT = Integer.parseInt(System.getProperty(TestApp.Properties.SERVER_PORT, "8080"));
    private final static String LOCAL_URL = String.format("http://host.testcontainers.internal:%d", SERVER_PORT);
    private final static String REPORT_DIRECTORY = System.getProperty(TestApp.Selenide.REPORT_DIR, Selenide.Defaults.REPORT_DIR);
    protected final static String BASE_URL = System.getProperty(TestApp.Properties.TEST_URL, LOCAL_URL);

    private final static String BUILD_NAME = System.getProperty(TestApp.Properties.BUILD_NAME, Selenide.Defaults.BUILD_NAME);

    private static String testName;

    @Rule
    public final TestRule watchman = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            super.starting(description);
            testName = setTestNameFromTestDescription(description);
            log.info(String.format("Starting build '%s'. Test: '%s", BUILD_NAME, testName));
        }

        @Override
        protected void succeeded(Description description) {
            super.succeeded(description);
            Cookie cookie = new Cookie("zaleniumTestPassed", "true");
            getDriver().manage().addCookie(cookie);
        }

        @Override
        protected void failed(Throwable e, Description description) {
            Cookie cookie = new Cookie("zaleniumTestPassed", "false");
            getDriver().manage().addCookie(cookie);

        }
    };

    @Before
    public void setup() throws Exception {
        addTestNameToDriver();

        super.setup();
        Parameters.setScreenshotErrorDirectory(REPORT_DIRECTORY);

        getDriver().get(BASE_URL);
    }

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

    protected abstract E openView();

    private String setTestNameFromTestDescription(Description description) {
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
