package eu.yals.test.utils.pages.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page https://www.foreca.fi, which is displayed when you visit to https://sää.fi
 *
 * @since 2.5
 */
public class ForecaFi {
    public static final SelenideElement LOGO = $(".logo a");
    public static final String LOGO_TITLE = "Foreca";
}
