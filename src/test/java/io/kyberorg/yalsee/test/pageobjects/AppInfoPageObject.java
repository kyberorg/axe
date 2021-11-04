package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.dev.AppInfoView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link AppInfoView}.
 *
 * @since 2.7
 */
public class AppInfoPageObject {

    public static class PublicInfoArea {
        public static final SelenideElement PUBLIC_INFO_AREA = $("#" + AppInfoView.IDs.PUBLIC_INFO_AREA);
        public static final SelenideElement VERSION = $("#" + AppInfoView.IDs.VERSION);
        public static final SelenideElement COMMIT_LINK = $("#" + AppInfoView.IDs.COMMIT_LINK);
        public static final SelenideElement GOOGLE_ANALYTICS_BANNER = $("#" + AppInfoView.IDs.GOOGLE_ANALYTICS_BANNER);
    }

    public static final class CookieArea {
        public static final SelenideElement COOKIE_AREA = $("#" + AppInfoView.IDs.COOKIE_AREA);
        public static final SelenideElement TITLE = $("#" + AppInfoView.IDs.COOKIE_TITLE);
        public static final SelenideElement TEXT_SECTION = $("#" + AppInfoView.IDs.COOKIE_TEXT_SPAN);
        public static final SelenideElement LINK = $("#" + AppInfoView.IDs.COOKIE_LINK);
        public static final SelenideElement TECH_DETAILS_TEXT = $("#" + AppInfoView.IDs.COOKIE_TECH_DETAILS);
        public static final SelenideElement CURRENT_SETTING_TITLE =
                $("#" + AppInfoView.IDs.COOKIE_CURRENT_SETTINGS_TITLE);
        public static final SelenideElement TECH_COOKIE_SPAN = $("#" + AppInfoView.IDs.TECH_COOKIE_SPAN);
        public static final SelenideElement TECH_COOKIE_LABEL = $("#" + AppInfoView.IDs.TECH_COOKIE_LABEL);
        public static final SelenideElement TECH_COOKIE_VALUE = $("#" + AppInfoView.IDs.TECH_COOKIE_VALUE);
        public static final SelenideElement ANALYTICS_COOKIE_SPAN = $("#" + AppInfoView.IDs.ANALYTICS_COOKIE_SPAN);
        public static final SelenideElement ANALYTICS_COOKIE_LABEL = $("#" + AppInfoView.IDs.ANALYTICS_COOKIE_LABEL);
        public static final SelenideElement ANALYTICS_COOKIE_VALUE = $("#" + AppInfoView.IDs.ANALYTICS_COOKIE_VALUE);

    }
}
