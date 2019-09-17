package ee.yals.test.utils.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Set of constant values with CSS selectors from page https://vr.fi
 *
 * @since 2.2
 */
public class VR {
    public static final SelenideElement LOGO = $("a.mainLogo");
}
