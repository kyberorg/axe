package ee.yals.test.utils;

/**
 * Selenide properties values and browser constants collected together for better documentation
 *
 * @since 2.0
 */
public class Selenide {

    private Selenide() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Props {
        static final String BROWSER = "selenide.browser";
        public static final String TIMEOUT = "selenide.timeout";
        public static final String BASE_URL = "selenide.baseUrl";
        public static final String REPORT_DIR = "yals.selenide.reportdir";
        public static final String SERVER_PORT = "port";
        public static final String TEST_URL = "testUrl";
    }

    @SuppressWarnings("unused") //documentation use only
    public static class Browser {
        public static final String HTMLUNIT = "htmlunit";
        public static final String CHROME = "chrome"; //needs chrome driver
        public static final String GECKO = "marionette"; //needs gecko driver
        public static final String FIREFOX = "firefox"; //not valid for Firefox 48+
    }

    public static class Defaults {
        public static final String REPORT_DIR = "target";
    }

}
