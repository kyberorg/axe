package eu.yals.test.app;

import eu.yals.test.TestApp;
import eu.yals.test.TestUtils;
import org.junit.BeforeClass;

/**
 * Tests, where we run application same ways in {@link eu.yals.test.ui.UITest}
 * and test by doing requests using {@link com.mashape.unirest.http.Unirest}
 *
 * @since 2.5.1
 */
public class UnirestTest {
    static final String TEST_URL = TestUtils.getTestUrl();

    @BeforeClass
    public static void setUp() {
        System.setProperty(TestApp.Properties.RUN_MODE, TestApp.RunMode.LOCAL.name());
    }
}
