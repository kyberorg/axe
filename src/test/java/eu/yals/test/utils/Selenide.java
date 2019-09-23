package eu.yals.test.utils;

/**
 * Selenide properties values and browser constants collected together for better documentation
 *
 * @since 2.0
 */
public class Selenide {

    private Selenide() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class Browser {
        public static final String HTMLUNIT = "htmlunit";
    }

    public static class Defaults {
        public static final String REPORT_DIR = "target";
        public static final String TIMEOUT = "4000";
    }

}
