package ee.yals.test.utils.pages.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page https://vr.fi
 *
 * @since 2.2
 */
public class VR {
    public static final SelenideElement LOGO = $("a.mainLogo");
}
