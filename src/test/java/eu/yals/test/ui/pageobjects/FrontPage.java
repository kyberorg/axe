package eu.yals.test.ui.pageobjects;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Set of constant values with CSS selectors from index.ftl
 *
 * @since 2.2
 */
public class FrontPage {
    public static class ErrorRow {
        public static final SelenideElement ERROR_MODAL = $("#errorModal");
        public static final SelenideElement ERROR_TEXT = $("#errorText");
        public static final SelenideElement ERROR_CLOSE = $("#errorClose");
    }

    public static class MainRow {
        public static final SelenideElement MAIN_DIV = $("#main");
        public static final SelenideElement H2 = $("#main h2");
        public static final SelenideElement FORM = $("form");
        public static final String INPUT_ID = "#longUrl";
        public static final SelenideElement LONG_URL_INPUT = $("#longUrl");
        public static final SelenideElement SUBMIT_BUTTON = $("#shortenIt");
        public static final SelenideElement PUBLIC_ACCESS_BANNER = $("#publicAccessBanner");
    }

    public static class OverallRow {
        public static final SelenideElement OVERALL_DIV = $("#overallLinks");
        public static final SelenideElement OVERALL_LINKS_TEXT = $("#overallLinksText");
        public static final SelenideElement OVERALL_LINKS_NUMBER = $("#overallLinksNum");
    }

    public static class ResultRow {
        public static final SelenideElement RESULT_DIV = $("#result");
        public static final SelenideElement RESULT_LINK = $("#resultLink");
        public static final SelenideElement COPY_RESULT_ICON = $("#copyLink");
    }

    public static class Footer {
        public static final SelenideElement FOOTER = $("footer");
        public static final SelenideElement VERSION = $("#version");
        public static final SelenideElement COMMIT_LINK = $("#version a");

    }
}
