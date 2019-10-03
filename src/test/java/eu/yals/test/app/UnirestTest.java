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
    public static final String TEST_URL = TestUtils.getTestUrl();

    @BeforeClass
    public static void setUp() {
        //application runs and accessible locally aka localhost
        System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name());
    }
}
