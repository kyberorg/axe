package eu.yals.test.ui.pageobjects.external;

/**
 * Page object for page https://ru.wikipedia.org/wiki/...
 *
 * @since 2.5
 */
public class Wikipedia {
    public static final String ARTICLE_TITLE =
            "Депортации из Эстонской Советской Социалистической Республики";

    @SuppressWarnings("SameReturnValue")
    public static String getArticleTitle() {
        return "#firstHeading";
    }
}
