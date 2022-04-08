package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.special.RedirectPage;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link RedirectPage}.
 *
 * @since 3.0.5
 */
public final class RedirectPageObject {

    private RedirectPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final SelenideElement VIEW = $("#" + RedirectPage.IDs.VIEW_ID);

    public static final SelenideElement DIRECT_ACCESS_BANNER = $("#" + RedirectPage.IDs.DIRECT_ACCESS_BANNER);
    public static final SelenideElement REDIRECT_PAGE_CONTAINER = $(RedirectPage.IDs.REDIRECT_PAGE);

    public static final class Links {
        public static final SelenideElement ORIGIN_LINK = $("#" + RedirectPage.IDs.ORIGIN_LINK_ID);
        public static final SelenideElement TARGET_LINK = $("#" + RedirectPage.IDs.TARGET_LINK_ID);
        public static final SelenideElement HERE_LINK = $("#" + RedirectPage.IDs.HERE_LINK_ID);
    }

    public static final class Elements {
        public static final SelenideElement NB = $("#" + RedirectPage.IDs.NB);
        public static final SelenideElement BYPASS_SYMBOL = $("#" + RedirectPage.IDs.BYPASS_SYMBOL_ID);
        public static final SelenideElement COUNTER = $("#" + RedirectPage.IDs.COUNTER_ID);
        public static final SelenideElement LEN_DIFF_STRING = $("#" + RedirectPage.IDs.LEN_DIFF_ID);
    }
}
