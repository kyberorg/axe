package eu.yals.test.ui.pageobjects.uus;

import com.codeborne.selenide.SelenideElement;
import eu.yals.ui.err.NotFoundView;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for {@link NotFoundView}. Contains elements from NotFoundView
 *
 * @since 2.7
 */
public class NotFoundViewPageObject {
    public static final SelenideElement TITLE = $("h1");
}
