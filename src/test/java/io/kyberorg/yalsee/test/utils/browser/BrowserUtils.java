package io.kyberorg.yalsee.test.utils.browser;

/**
 * Browser-related utils.
 *
 * @since 3.0.7
 */
public final class BrowserUtils {

    /**
     * Maximum screen width below which device considered as extra small (portrait phones).
     */
    public static final int EXTRA_SMALL_SCREEN_MAX_WIDTH_PIXELS = 576;

    /**
     * Provides {@link BrowserSize} object, which has info from {@link com.codeborne.selenide.Configuration}.
     *
     * @return {@link BrowserSize} object with parsed height and width.
     */
    public static BrowserSize getBrowserSize() {
        return BrowserSize.fromSelenideConfiguration();
    }

    private BrowserUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
}
