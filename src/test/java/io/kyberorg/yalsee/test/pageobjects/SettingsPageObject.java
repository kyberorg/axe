package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.SettingsPage;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link SettingsPage}.
 *
 * @since 3.8
 */
public class SettingsPageObject {
    public static final SelenideElement PAGE_ID = $("#" + SettingsPage.IDs.PAGE_ID);
    public static final SelenideElement PAGE_TITLE = $("#" + SettingsPage.IDs.PAGE_TITLE);

    public static final class CookieSettings {
        public static final SelenideElement TITLE = $("#" + SettingsPage.IDs.COOKIE_SETTINGS_TITLE);
        public static final SelenideElement TECH_COOKIE_SPAN = $("#" + SettingsPage.IDs.TECH_COOKIE_SPAN);
        public static final SelenideElement TECH_COOKIE_LABEL = $("#" + SettingsPage.IDs.TECH_COOKIE_LABEL);
        public static final SelenideElement TECH_COOKIE_VALUE = $("#" + SettingsPage.IDs.TECH_COOKIE_VALUE);
        public static final SelenideElement ANALYTICS_COOKIE_SPAN =
                $("#" + SettingsPage.IDs.ANALYTICS_COOKIE_SPAN);
        public static final SelenideElement ANALYTICS_COOKIE_LABEL =
                $("#" + SettingsPage.IDs.ANALYTICS_COOKIE_LABEL);
        public static final SelenideElement ANALYTICS_COOKIE_VALUE =
                $("#" + SettingsPage.IDs.ANALYTICS_COOKIE_VALUE);
        public static final SelenideElement ANALYTICS_COOKIE_POSTFIX_SPAN =
                $("#" + SettingsPage.IDs.ANALYTICS_COOKIE_SPAN + " " + PageReloadPostfix.SPAN);
        public static final SelenideElement ANALYTICS_COOKIE_POSTFIX_BUTTON =
                $("#" + SettingsPage.IDs.ANALYTICS_COOKIE_SPAN + " " + PageReloadPostfix.BUTTON);
    }

    public static final class PageReloadPostfix {
        public static final String SPAN = "." + SettingsPage.Classes.POSTFIX;
        public static final String BUTTON = "." + SettingsPage.Classes.PAGE_RELOAD_BUTTON;
    }
}
