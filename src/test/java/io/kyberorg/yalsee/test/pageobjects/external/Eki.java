package io.kyberorg.yalsee.test.pageobjects.external;

/**
 * Page object for page http://eki.ee/dict/ekss/index.cgi
 *
 * @since 2.5
 */
@SuppressWarnings("SpellCheckingInspection")
public final class Eki {
    public static final String TITLE_TEXT =
            "[EKSS] \"Eesti keele seletav sõnaraamat\" 2009";

    /**
     * Selector for title.
     *
     * @return string with CSS selector holding page title
     */
    @SuppressWarnings("SameReturnValue")
    public static String getTitle() {
        return "#title";
    }

    private Eki() {
        throw new UnsupportedOperationException("Utility class");
    }
}
