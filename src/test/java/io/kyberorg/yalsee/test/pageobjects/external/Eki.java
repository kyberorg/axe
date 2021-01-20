package io.kyberorg.yalsee.test.pageobjects.external;

/**
 * Page object for page http://eki.ee/dict/ekss/index.cgi
 *
 * @since 2.5
 */
@SuppressWarnings("SpellCheckingInspection")
public class Eki {
    public static final String TITLE_TEXT =
            "[EKSS] \"Eesti keele seletav sõnaraamat\" 2009";

    @SuppressWarnings("SameReturnValue")
    public static String getTitle() {
        return "#title";
    }
}