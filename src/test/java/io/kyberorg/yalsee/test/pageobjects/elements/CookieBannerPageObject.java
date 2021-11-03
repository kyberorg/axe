package io.kyberorg.yalsee.test.pageobjects.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.components.CookieBanner;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Page object for {@link CookieBanner}.
 *
 * @since 3.5
 */
public final class CookieBannerPageObject {
    public static final SelenideElement DIALOG = $("#" + CookieBanner.IDs.CB_DIALOG);
    public static final SelenideElement TITLE = $("#" + CookieBanner.IDs.CB_TITLE);

    public static final class BannerText {
        public static final SelenideElement TEXT = $("#" + CookieBanner.IDs.CB_TEXT);
        public static final SelenideElement LINK = $("#" + CookieBanner.IDs.CB_LINK);
    }

    public static final class Boxes {
        public static final ElementsCollection BOXES = $$("." + CookieBanner.Classes.CB_BOX);
        public static final SelenideElement ONLY_NECESSARY_BOX = $("#" + CookieBanner.IDs.CB_ONLY_NECESSARY_BOX);
        public static final SelenideElement ANALYTICS_BOX = $("#" + CookieBanner.IDs.CB_ANALYTICS_BOX);
    }

    public static final class Buttons {
        public static final ElementsCollection BUTTONS = $$("." + CookieBanner.Classes.CB_BUTTON);
        public static final SelenideElement ONLY_NECESSARY_BUTTON = $("#" + CookieBanner.IDs.CB_ONLY_NECESSARY_BUTTON);
        public static final SelenideElement SELECTION_BUTTON = $("#" + CookieBanner.IDs.CB_SELECTION_BUTTON);
        public static final SelenideElement ALLOW_ALL_BUTTON = $("#" + CookieBanner.IDs.CB_ALLOW_ALL_BUTTON);
    }

    public static void closeBannerIfAny() {
        if (isBannerDisplayed()) {
            DIALOG.pressEscape();
        } else {
            $("body").pressEscape();
        }
    }

    public static boolean isBannerDisplayed() {
        return DIALOG.isDisplayed();
    }

    public static boolean isBannerHidden() {
        return !isBannerDisplayed();
    }
}
