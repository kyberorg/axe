package io.kyberorg.yalsee.test.utils.browser;

import com.codeborne.selenide.Configuration;
import lombok.Data;

/**
 * Object that contains browser's height and width.
 *
 * @since 3.0.7
 */
@Data
public final class BrowserSize {
    public static final int INVALID_SIZE = -2;

    private int height;
    private int width;

    /**
     * Creates {@link BrowserSize} object, based on Selenide's {@link Configuration}.
     *
     * @return {@link BrowserSize} object with information from Selenide's {@link Configuration}.
     */
    public static BrowserSize fromSelenideConfiguration() {
        return fromString(Configuration.browserSize);
    }

    private static BrowserSize fromString(final String sizeString) {
        BrowserSize browserSize = new BrowserSize();
        //parse string
        String[] parts = sizeString.split("x");
        if (parts.length == 2) {
            browserSize.width = Integer.parseInt(parts[0]);
            browserSize.height = Integer.parseInt(parts[1]);
        } else {
            browserSize.width = INVALID_SIZE;
            browserSize.height = INVALID_SIZE;
        }
        return browserSize;
    }

    private BrowserSize() {

    }
}
