package io.kyberorg.yalsee.test.pageobjects.external;

/**
 * Page object for page https://ru.wikipedia.org/wiki/...
 *
 * @since 2.5
 */
public final class Wikipedia {
    public static final String ARTICLE_TITLE =
            "Депортации из Эстонской ССР";

    /**
     * Selector for article title.
     *
     * @return string with CSS selector
     */
    @SuppressWarnings("SameReturnValue")
    public static String getArticleTitle() {
        return "#firstHeading";
    }

    private Wikipedia() {
        throw new UnsupportedOperationException("Utility class");
    }
}
