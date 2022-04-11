package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.TextFieldElement;
import io.kyberorg.yalsee.ui.pages.home.HomePage;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.utils.TestUtils.addRedirectPageBypassSymbol;

/**
 * Page Object for {@link HomePage}.
 *
 * @since 2.2
 */
public final class HomePageObject {

    private HomePageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class MainArea {
        public static final SelenideElement MAIN_AREA = $("#" + HomePage.IDs.MAIN_AREA);
        public static final SelenideElement TITLE = $("#" + HomePage.IDs.TITLE);
        public static final SelenideElement LONG_URL_INPUT_LABEL =
                TextFieldElement.byCss("#" + HomePage.IDs.INPUT).getLabel();
        public static final SelenideElement LONG_URL_INPUT =
                TextFieldElement.byCss("#" + HomePage.IDs.INPUT).getInput();
        public static final SelenideElement DESCRIPTION_ACCORDION =
                $("#" + HomePage.IDs.DESCRIPTION_ACCORDION);
        public static final SelenideElement DESCRIPTION_ACCORDION_PANEL =
                DESCRIPTION_ACCORDION.$("vaadin-accordion-panel");
        public static final SelenideElement DESCRIPTION_INPUT_ELEMENT =
                $("#" + HomePage.IDs.DESCRIPTION_INPUT);
        public static final SelenideElement DESCRIPTION_INPUT =
                TextFieldElement.byCss("#" + HomePage.IDs.DESCRIPTION_INPUT).getInput();
        public static final SelenideElement BANNER = $("#" + HomePage.IDs.BANNER);
        public static final SelenideElement SUBMIT_BUTTON = $("#" + HomePage.IDs.SUBMIT_BUTTON);
    }

    public static class OverallArea {
        public static final SelenideElement OVERALL_AREA = $("#" + HomePage.IDs.OVERALL_AREA);
        public static final SelenideElement OVERALL_LINKS_TEXT = $("#" + HomePage.IDs.OVERALL_LINKS_TEXT);
        public static final SelenideElement OVERALL_LINKS_NUMBER = $("#" + HomePage.IDs.OVERALL_LINKS_NUMBER);
    }

    public static class ResultArea {
        public static final SelenideElement RESULT_AREA = $("#" + HomePage.IDs.RESULT_AREA);
        public static final SelenideElement RESULT_LINK = $("#" + HomePage.IDs.SHORT_LINK);
        public static final SelenideElement COPY_LINK_ICON = $("#" + HomePage.IDs.COPY_LINK_BUTTON);
    }

    public static class QrCodeArea {
        public static final SelenideElement QR_CODE_AREA = $("#" + HomePage.IDs.QR_CODE_AREA);
        public static final SelenideElement QR_CODE = $("#" + HomePage.IDs.QR_CODE);
    }

    public static class MyLinksNoteArea {
        public static final SelenideElement MY_LINKS_NOTE_AREA = $("#" + HomePage.IDs.MY_LINKS_NOTE_AREA);
        public static final SelenideElement MY_LINKS_NOTE_TEXT = $("#" + HomePage.IDs.MY_LINKS_NOTE_TEXT);
        public static final SelenideElement MY_LINKS_NOTE_LINK = $("#" + HomePage.IDs.MY_LINKS_NOTE_LINK);
        public static final SelenideElement MY_LINKS_NOTE_POST_TEXT =
                $("#" + HomePage.IDs.MY_LINKS_NOTE_END);
    }

    public static class ErrorModal {
        public static final SelenideElement ERROR_MODAL =
                $("#vaadin-notification-card");
        public static final SelenideElement ERROR_TEXT =
                ERROR_MODAL.$("flow-component-renderer div vaadin-horizontal-layout label");
        public static final SelenideElement ERROR_BUTTON =
                ERROR_MODAL.$("flow-component-renderer div vaadin-horizontal-layout vaadin-button");
    }

    /**
     * Number of links saved in application.
     *
     * @return non-negative long with number of saved links.
     */
    public static long getNumberOfSavedLinks() {
        long linksCount;
        try {
            linksCount = Long.parseLong(OverallArea.OVERALL_LINKS_NUMBER.getText());
        } catch (NumberFormatException e) {
            linksCount = 0;
        }
        return linksCount;
    }

    /**
     * Pastes link to input.
     *
     * @param link string with long URL to paste
     */
    public static void pasteValueInForm(final String link) {
        MainArea.LONG_URL_INPUT.setValue(link);
    }

    /**
     * Pastes link to input and clicks submit button.
     *
     * @param link string with long URL to paste
     */
    public static void pasteValueInFormAndSubmitIt(final String link) {
        pasteValueInForm(link);
        MainArea.SUBMIT_BUTTON.click();
        waitForVaadin();
    }

    /**
     * Pastes link to input, clicks submit button and returns result link.
     *
     * @param urlToStore String with long URL to store it
     * @return string with short URL produced.
     */
    public static String storeAndReturnSavedUrl(final String urlToStore) {
        pasteValueInFormAndSubmitIt(urlToStore);
        return HomePageObject.ResultArea.RESULT_LINK.getText();
    }

    /**
     * Pastes link to input, clicks submit button and opens result link in browser window.
     *
     * @param urlToStore string with long URL to store it
     */
    public static void storeAndOpenSavedUrl(final String urlToStore) {
        String shortLink = storeAndReturnSavedUrl(urlToStore);
        open(shortLink + addRedirectPageBypassSymbol());
    }

    /**
     * Saves given url.
     *
     * @param url string with long URL.
     */
    public static void saveOneLink(final String url) {
        open("/");
        waitForVaadin();
        pasteValueInFormAndSubmitIt(url);
    }

    /**
     * Saves given URL with given description.
     *
     * @param url         non-empty string with long link
     * @param description non-empty string with description.
     */
    public static void saveLinkWithDescription(final String url, final String description) {
        Assertions.assertTrue(StringUtils.isNotBlank(url), "Got empty long link");
        Assertions.assertTrue(StringUtils.isNotBlank(description), "Got empty description. "
                + "This method excepts non-empty description");

        open("/");
        waitForVaadin();
        pasteValueInForm(url);
        MainArea.DESCRIPTION_ACCORDION.click();
        MainArea.DESCRIPTION_INPUT.setValue(description);

        MainArea.SUBMIT_BUTTON.click();
        waitForVaadin();
    }

    /**
     * Retrieves result link from element.
     *
     * @return string with short URL
     */
    public static String getSavedUrl() {
        return ResultArea.RESULT_LINK.getText();
    }
}
