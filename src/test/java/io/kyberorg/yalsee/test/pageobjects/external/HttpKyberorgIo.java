package io.kyberorg.yalsee.test.pageobjects.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page <a href="http://http.kyberorg.io">http://http.kyberorg.io</a> .
 */
public final class HttpKyberorgIo {
    public static final String TITLE_TEXT = "Welcome to Nginx!";

    public static final SelenideElement TITLE = $("h1");

    private HttpKyberorgIo() {
        throw new UnsupportedOperationException("Utility class");
    }
}
