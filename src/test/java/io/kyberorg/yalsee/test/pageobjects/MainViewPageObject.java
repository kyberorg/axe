package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.MainView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for element at {@link MainView}.
 *
 * @since 2.8
 */
public final class MainViewPageObject {

    private MainViewPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final SelenideElement LOGO = $("#" + MainView.IDs.APP_LOGO);
    public static final SelenideElement GOOGLE_ANALYTICS_CONTROL_SPAN = $("#gtag");

    public static final SelenideElement USER_BUTTON = $("#" + MainView.IDs.USER_BUTTON);
    public static final SelenideElement USER_MENU = $("vaadin-context-menu-list-box");
    public static final SelenideElement LOGIN_BUTTON = $("#" + MainView.IDs.LOGIN_BUTTON);
    public static final SelenideElement REGISTER_BUTTON = $("#" + MainView.IDs.REGISTER_BUTTON);
}
