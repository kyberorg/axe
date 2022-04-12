package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.pages.appinfo.AppInfoPage;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link AppInfoPage}.
 *
 * @since 2.7
 */
public class AppInfoPageObject {

    public static class PublicInfoArea {
        public static final SelenideElement PUBLIC_INFO_AREA = $("#" + AppInfoPage.IDs.PUBLIC_INFO_AREA);
        public static final SelenideElement TITLE = $("#publicInfoTitle");
        public static final SelenideElement VERSION = $("#" + AppInfoPage.IDs.VERSION);
        public static final SelenideElement COMMIT_LINK = $("#" + AppInfoPage.IDs.COMMIT_LINK);
        public static final SelenideElement GOOGLE_ANALYTICS_BANNER = $("#" + AppInfoPage.IDs.GOOGLE_ANALYTICS_BANNER);
    }

    public static final class CookieArea {
        public static final SelenideElement COOKIE_AREA = $("#" + AppInfoPage.IDs.COOKIE_AREA);
        public static final SelenideElement TITLE = $("#" + AppInfoPage.IDs.COOKIE_TITLE);
        public static final SelenideElement TEXT_SECTION = $("#" + AppInfoPage.IDs.COOKIE_TEXT_SPAN);
        public static final SelenideElement LINK = $("#" + AppInfoPage.IDs.COOKIE_LINK);
        public static final SelenideElement TECH_DETAILS_TEXT = $("#" + AppInfoPage.IDs.COOKIE_TECH_DETAILS);
        public static final SelenideElement COOKIE_SETTING_SPAN = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_SPAN);
        public static final SelenideElement COOKIE_SETTINGS_TEXT = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_TEXT);
        public static final SelenideElement COOKIE_SETTINGS_LINK = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_LINK);
        public static final SelenideElement COOKIE_SETTINGS_POINT = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_POINT);
    }
}
