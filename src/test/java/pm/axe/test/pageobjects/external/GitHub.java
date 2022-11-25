package pm.axe.test.pageobjects.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page https://github.com/kyberorg/axe/issues/353 .
 *
 * @since 3.0.5
 */
public final class GitHub {

    public static final SelenideElement GITHUB_HEADER = $(".gh-header");

    private GitHub() {
        throw new UnsupportedOperationException("Utility class");
    }
}
