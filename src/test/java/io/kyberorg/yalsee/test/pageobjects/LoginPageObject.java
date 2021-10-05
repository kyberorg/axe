package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.user.LoginView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link LoginView}.
 *
 * @since 4.0
 */
public final class LoginPageObject {

    private LoginPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public final static SelenideElement PAGE_ID = $("#" + LoginView.IDs.PAGE_ID);
}
