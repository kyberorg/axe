package pm.axe.test.pageobjects.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page https://sõnaveeb.ee .
 *
 * @since 2.5
 */
@SuppressWarnings("SpellCheckingInspection")
public final class SonaveebEe {
    public static final String LOGO_ALT_TEXT = "Sõnaveeb Logo";

    public static final SelenideElement LOGO = $("img.logo");
    private SonaveebEe() {
        throw new UnsupportedOperationException("Utility class");
    }
}
