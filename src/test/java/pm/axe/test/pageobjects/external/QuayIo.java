package pm.axe.test.pageobjects.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page https://quay.io .
 *
 * @since 3.4
 */
public final class QuayIo {
    public static final SelenideElement LOGO = $("#quay-logo");

    private QuayIo() {
        throw new UnsupportedOperationException("Utility class");
    }
}
