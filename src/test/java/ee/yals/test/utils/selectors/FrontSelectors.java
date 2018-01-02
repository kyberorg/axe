package ee.yals.test.utils.selectors;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Set of constant values with CSS selectors from index.ftl
 *
 * @since 2.2
 */
public class FrontSelectors {
    public static class AuthRow {
        public static final SelenideElement AUTH_DIV = $("#auth");
        public static final SelenideElement MY_YALS_LOGO = $("#myYalsLogo");
        public static final SelenideElement WHY_LINK = $("#whyLink");
        public static final SelenideElement LOGIN_BUTTON = $("#loginButton");
    }
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
        public static final SelenideElement LONG_URL_INPUT = $(INPUT_ID);
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

    public static class WhyModal {
        public static final SelenideElement WHY_MODAL = $("#whyModal");
        public static final SelenideElement WHY_MODAL_TITLE = WHY_MODAL.$(".modal-title");
        public static final SelenideElement WHY_MODAL_BODY = WHY_MODAL.$(".modal-body");
        public static final SelenideElement WHY_MODAL_BUTTONS = WHY_MODAL.$(".modal-footer");
        public static final SelenideElement WHY_MODAL_CLOSE_BUTTON = WHY_MODAL_BUTTONS.$("button.btn-close");
        public static final SelenideElement WHY_MODAL_DEMO_BUTTON = WHY_MODAL_BUTTONS.$("button.btn-primary");
    }
}
