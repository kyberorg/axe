package ee.yals.test.utils;

/**
 * Selenide properties values and browser constants collected together for better documentation
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class Selenide {

    private Selenide() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Props {
        public static final String BROWSER = "selenide.browser";
        public static final String TIMEOUT = "selenide.timeout";
        public static final String BASE_URL = "selenide.baseUrl";

        @SuppressWarnings("unused") //documentation use only
        public static final String CHROME_WEBDRIVER = "webdriver.chrome.driver";
    }

    @SuppressWarnings("unused") //documentation use only
    public static class Browser {
        public static final String HTMLUNIT = "htmlunit";
        public static final String CHROME = "chrome"; //needs chrome driver
        public static final String GECKO = "marionette"; //needs gecko driver
        public static final String FIREFOX = "firefox"; //not valid for Firefox 48+
    }


}
