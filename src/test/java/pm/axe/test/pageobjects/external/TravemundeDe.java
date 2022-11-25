package pm.axe.test.pageobjects.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page http://www.travemünde.de/ .
 *
 * @since 2.5
 */
@SuppressWarnings("SpellCheckingInspection")
public final class TravemundeDe {
    public static final SelenideElement BODY = $("body.travemuende");

    private TravemundeDe() {
        throw new UnsupportedOperationException("Utility class");
    }
}
