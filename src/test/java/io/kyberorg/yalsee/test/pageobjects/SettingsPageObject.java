package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.pages.settings.SettingsPage;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link SettingsPage}.
 *
 * @since 3.8
 */
public final class SettingsPageObject {
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

    public static final class BetaSettings {
        public static final SelenideElement TITLE = $("#" + SettingsPage.IDs.BETA_SETTINGS_TITLE);
        public static final SelenideElement DARK_MODE_SPAN = $("#" + SettingsPage.IDs.DARK_MODE_SPAN);
        public static final SelenideElement DARK_MODE_LABEL = $("#" + SettingsPage.IDs.DARK_MODE_LABEL);
        public static final SelenideElement DARK_MODE_VALUE = $("#" + SettingsPage.IDs.DARK_MODE_VALUE);
    }

    public static final class PageReloadPostfix {
        public static final String SPAN = "." + SettingsPage.Classes.POSTFIX;
        public static final String BUTTON = "." + SettingsPage.Classes.PAGE_RELOAD_BUTTON;
    }

    /**
     * Checks if dark mode is enabled.
     */
    public static void darkModeShouldBeEnabled() {
        $("html").shouldHave(attribute("theme", "dark"));
    }

    /**
     * Checks if light (original) mode is enabled.
     */
    public static void defaultModeShouldBeEnabled() {
        final String theme = $("html").getAttribute("theme");
        final boolean isDefaultModeEnabled = StringUtils.isBlank(theme) || theme.equals("light");
        Assertions.assertTrue(isDefaultModeEnabled);
    }

    /**
     * Is Site working with dark mode on.
     *
     * @return true if site is dark, false if not.
     */
    public static boolean isDarkModeActive() {
        final String theme = $("html").getAttribute("theme");
        return StringUtils.isNotBlank(theme) && theme.equals("dark");
    }

    private SettingsPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }
}
