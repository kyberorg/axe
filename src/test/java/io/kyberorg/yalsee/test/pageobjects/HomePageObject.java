package io.kyberorg.yalsee.test.pageobjects;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.utils.vaadin.elements.TextFieldElement;
import io.kyberorg.yalsee.ui.HomeView;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.TestUtils.addRedirectPageBypassSymbol;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Page Object for {@link HomeView}.
 *
 * @since 2.2
 */
public final class HomePageObject {

    private HomePageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class MainArea {
        public static final SelenideElement MAIN_AREA = $("#" + HomeView.IDs.MAIN_AREA);
        public static final SelenideElement TITLE = $("#" + HomeView.IDs.TITLE);
        public static final SelenideElement LONG_URL_INPUT_LABEL =
                TextFieldElement.byCss("#" + HomeView.IDs.INPUT).getLabel();
        public static final SelenideElement LONG_URL_INPUT =
                TextFieldElement.byCss("#" + HomeView.IDs.INPUT).getInput();
        public static final SelenideElement BANNER = $("#" + HomeView.IDs.BANNER);
        public static final SelenideElement SUBMIT_BUTTON = $("#" + HomeView.IDs.SUBMIT_BUTTON);
    }

    public static class OverallArea {
        public static final SelenideElement OVERALL_AREA = $("#" + HomeView.IDs.OVERALL_AREA);
        public static final SelenideElement OVERALL_LINKS_TEXT = $("#" + HomeView.IDs.OVERALL_LINKS_TEXT);
        public static final SelenideElement OVERALL_LINKS_NUMBER = $("#" + HomeView.IDs.OVERALL_LINKS_NUMBER);
    }

    public static class ResultArea {
        public static final SelenideElement RESULT_AREA = $("#" + HomeView.IDs.RESULT_AREA);
        public static final SelenideElement RESULT_LINK = $("#" + HomeView.IDs.SHORT_LINK);
        public static final SelenideElement COPY_LINK_ICON = $("#" + HomeView.IDs.COPY_LINK_BUTTON);
    }

    public static class QrCodeArea {
        public static final SelenideElement QR_CODE_AREA = $("#" + HomeView.IDs.QR_CODE_AREA);
        public static final SelenideElement QR_CODE = $("#" + HomeView.IDs.QR_CODE);
    }

    public static class MyLinksNoteArea {
        public static final SelenideElement MY_LINKS_NOTE_AREA = $("#" + HomeView.IDs.MY_LINKS_NOTE_AREA);
        public static final SelenideElement MY_LINKS_NOTE_TEXT = $("#" + HomeView.IDs.MY_LINKS_NOTE_TEXT);
        public static final SelenideElement MY_LINKS_NOTE_LINK = $("#" + HomeView.IDs.MY_LINKS_NOTE_LINK);
        public static final SelenideElement MY_LINKS_NOTE_POST_TEXT =
                $("#" + HomeView.IDs.MY_LINKS_NOTE_POST_TEXT);
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
     * Retrieves result link from element.
     *
     * @return string with short URL
     */
    public static String getSavedUrl() {
        return ResultArea.RESULT_LINK.getText();
    }
}
