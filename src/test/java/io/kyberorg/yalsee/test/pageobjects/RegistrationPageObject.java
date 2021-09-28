package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.RegistrationView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link io.kyberorg.yalsee.ui.RegistrationView}.
 *
 * @since 4.0
 */
public final class RegistrationPageObject {

    private RegistrationPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public final static SelenideElement PAGE_ID = $("#" + RegistrationView.IDs.PAGE_ID);
}
