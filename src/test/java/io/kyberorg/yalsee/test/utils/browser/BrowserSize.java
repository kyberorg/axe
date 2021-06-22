package io.kyberorg.yalsee.test.utils.browser;

import com.codeborne.selenide.Configuration;
import lombok.Data;

/**
 * Object that contains browser's height and width.
 *
 * @since 3.0.7
 */
@Data
public class BrowserSize {
    public static final int INVALID_SIZE = -2;

    private int height;
    private int width;

    public static BrowserSize fromSelenideConfiguration() {
        return fromString(Configuration.browserSize);
    }

    private static BrowserSize fromString(final String sizeString) {
        BrowserSize browserSize = new BrowserSize();
        //parse string
        String[] parts = sizeString.split("x");
        if (parts.length == 2) {
            browserSize.width = Integer.getInteger(parts[0]);
            browserSize.height = Integer.getInteger(parts[1]);
        } else {
            browserSize.width = INVALID_SIZE;
            browserSize.height = INVALID_SIZE;
        }
        return browserSize;
    }

    private BrowserSize() { }


}
