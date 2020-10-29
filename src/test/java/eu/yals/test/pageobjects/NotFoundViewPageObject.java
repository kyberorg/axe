package eu.yals.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import eu.yals.ui.err.IdentNotFoundView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link IdentNotFoundView}. Contains elements from NotFoundView
 *
 * @since 2.7
 */
public class NotFoundViewPageObject {
    public static final SelenideElement TITLE = $("h1");
}
