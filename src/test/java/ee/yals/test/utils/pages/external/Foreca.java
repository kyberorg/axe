package ee.yals.test.utils.pages.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Set of constant values with CSS selectors from page https://www.foreca.fi,
 * which is displayed when you visit to https://sää.fi
 *
 * @since 2.5
 */
public class Foreca {
    public static final SelenideElement LOGO = $(".logo a");
    public static final String LOGO_TITLE = "Foreca";
}
