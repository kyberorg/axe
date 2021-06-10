package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.special.RedirectView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link RedirectView}.
 *
 * @since 3.0.5
 */
public final class RedirectPageObject {

    private RedirectPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final SelenideElement VIEW = $("#" + RedirectView.IDs.VIEW_ID);

    public static final SelenideElement DIRECT_ACCESS_BANNER = $("#" + RedirectView.IDs.DIRECT_ACCESS_BANNER);
    public static final SelenideElement MAIN_AREA = $(".main-area");

    public static final class Links {
        public static final SelenideElement ORIGIN_LINK = $("#" + RedirectView.IDs.ORIGIN_LINK_ID);
        public static final SelenideElement TARGET_LINK = $("#" + RedirectView.IDs.TARGET_LINK_ID);
        public static final SelenideElement HERE_LINK = $("#" + RedirectView.IDs.HERE_LINK_ID);
    }

    public static final class Elements {
        public static final SelenideElement NB = $("#" + RedirectView.IDs.NB);
        public static final SelenideElement BYPASS_SYMBOL = $("#" + RedirectView.IDs.BYPASS_SYMBOL_ID);
        public static final SelenideElement COUNTER = $("#" + RedirectView.IDs.COUNTER_ID);
        public static final SelenideElement LEN_DIFF_STRING = $("#" + RedirectView.IDs.LEN_DIFF_ID);
    }
}
