package eu.yals.test.utils.pages.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page http://€.linux.it
 *
 * @since 2.5
 */
public class EuroLinuxIt {
    public static final SelenideElement H1 = $("h1");
    public static final String H1_TEXT = "€.linux.it";
}
