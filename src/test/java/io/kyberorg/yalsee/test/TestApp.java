package io.kyberorg.yalsee.test;

import io.kyberorg.yalsee.constants.App;

/**
 * Test Application constants, pretty same as {@link App}, but only stuff used in testing scope
 *
 * @since 2.5
 */
public class TestApp {
    private TestApp() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Properties {
        public static final String TEST_URL = "test.url";
        public static final String APP_SHORT_URL = "app.shortUrl";
        public static final String TEST_RUN_MODE = "test.runMode";
        public static final String BUILD_NAME = "test.buildName";
        public static final String REPORT_DIR = "test.reportdir";
        public static final String GRID_HOSTNAME = "grid.hostname";

        public static class Selenide {
            public static final String BROWSER = "selenide.browser";
            public static final String REMOTE = "selenide.remote";
            public static final String TIMEOUT = "selenide.timeout";
        }
    }

    public static class Defaults {
        public static final String BUILD_NAME = TestUtils.hostName() + "-"+ TestUtils.timeStamp();

        public static class Selenide {
            public static final String REPORT_DIR = "target/reports";
            public static final String TIMEOUT = "4000";
            public static final String BROWSER = "chrome";
        }
    }

    public enum RunMode {
        GRID,
        LOCAL
    }

}
