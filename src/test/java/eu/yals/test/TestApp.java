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
        public static final String GRID_HOSTNAME = "grid.hostname";
    }

    public static class Defaults {
        public static final String SERVER_PORT = "8080";
        public static final String REPORT_DIR = "target";
        public static final String BUILD_NAME = "build-" + TestUtils.timeStamp();
    }

    public static class Selenide {
        public static final String BROWSER = "selenide.browser";
        public static final String REMOTE = "selenide.remote";
        public static final String TIMEOUT = "selenide.timeout";
        public static final String REPORT_DIR = "yals.selenide.report_dir";
    }

    public enum RunMode {
        CONTAINER,
        GRID,
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
