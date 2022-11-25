package pm.axe.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import pm.axe.ui.pages.appinfo.AppInfoPage;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link AppInfoPage}.
 *
 * @since 2.7
 */
public class AppInfoPageObject {

    public static class GeneralInfoSection {
        public static final SelenideElement SECTION = $("#" + AppInfoPage.IDs.GENERAL_INFO_SECTION);
        public static final SelenideElement TITLE = $("#" + AppInfoPage.IDs.GENERAL_INFO_SECTION_TITLE);
        public static final SelenideElement SPAN = $("#" + AppInfoPage.IDs.GENERAL_INFO_SPAN);
    }

    public static final class CookieSection {
        public static final SelenideElement SECTION = $("#" + AppInfoPage.IDs.COOKIE_SECTION);
        public static final SelenideElement TITLE = $("#" + AppInfoPage.IDs.COOKIE_TITLE);
        public static final SelenideElement TEXT_SECTION = $("#" + AppInfoPage.IDs.COOKIE_TEXT_SPAN);
        public static final SelenideElement LINK = $("#" + AppInfoPage.IDs.COOKIE_LINK);
        public static final SelenideElement TECH_DETAILS_TEXT = $("#" + AppInfoPage.IDs.COOKIE_TECH_DETAILS);
        public static final SelenideElement COOKIE_SETTING_SPAN = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_SPAN);
        public static final SelenideElement COOKIE_SETTINGS_TEXT = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_TEXT);
        public static final SelenideElement COOKIE_SETTINGS_LINK = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_LINK);
        public static final SelenideElement COOKIE_SETTINGS_POINT = $("#" + AppInfoPage.IDs.COOKIE_SETTINGS_POINT);
    }

    public static final class TechInfoSection {
        public static final SelenideElement SECTION = $("#" + AppInfoPage.IDs.TECH_INFO_SECTION);
        public static final SelenideElement TITLE = $("#" + AppInfoPage.IDs.TECH_INFO_TITLE);
        public static final SelenideElement VERSION = $("#" + AppInfoPage.IDs.VERSION);
        public static final SelenideElement COMMIT_LINK = $("#" + AppInfoPage.IDs.COMMIT_LINK);

    }
}
