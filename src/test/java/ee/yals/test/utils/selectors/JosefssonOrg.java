package ee.yals.test.utils.selectors;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Set of constant values with CSS selectors from page https://räksmörgås.josefsson.org/
 *
 * @since 2.5
 */
public class JosefssonOrg {
    public static final SelenideElement H1 = $("h1");
    public static final String H1_TEXT = "Räksmörgås.josefßon.org";
}
