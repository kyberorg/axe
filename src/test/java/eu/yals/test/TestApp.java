package eu.yals.test;

/**
 * Test Application constants, pretty same as {@link eu.yals.constants.App}, but only stuff used in testing scope
 *
 * @since 2.5
 */
public class TestApp {
    public static class Properties {
        public static final String TEST_URL = "test.url";
        public static final String SERVER_PORT = "port";
        public static final String TEST_RUN_MODE = "test.runMode";
        public static final String BUILD_NAME = "test.buildName";
        public static final String TEST_BROWSERS = "test.browsers";
        public static final String REPORT_DIR = "test.reportdir";
    }

    public static class Defaults {
        public static final String SERVER_PORT = "8080";
        public static final String REPORT_DIR = "target";
        public static final String BUILD_NAME = "build-" + TestUtils.timeStamp();
    }

    public enum RunMode {
        CONTAINER,
        LOCAL
    }

    public enum Browser {
        CHROME,
        FIREFOX,
        SAFARI,
        IE,
        EDGE
    }
}