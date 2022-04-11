package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.ui.pages.err.ident404.IdentNotFoundPage;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link IdentNotFoundPage}. Contains elements from NotFoundView.
 *
 * @since 2.7
 */
public final class NotFoundViewPageObject {

    public static final SelenideElement TITLE = $("h1");

    private NotFoundViewPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }
}
