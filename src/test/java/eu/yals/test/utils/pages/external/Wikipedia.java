package eu.yals.test.utils.pages.external;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for page https://ru.wikipedia.org/wiki/...
 *
 * @since 2.5
 */
public class Wikipedia {
    public static final String ARTICLE_TITLE = "Депортации из Эстонской Советской Социалистической Республики";

    public static SelenideElement getArticleTitle() {
        return $("#firstHeading");
    }
}
