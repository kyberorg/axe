package pm.axe.test.pageobjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;
import pm.axe.test.utils.vaadin.elements.TextFieldElement;
import pm.axe.ui.pages.home.HomePage;

import static com.codeborne.selenide.Selenide.*;
import static pm.axe.test.pageobjects.VaadinPageObject.waitForVaadin;
import static pm.axe.test.utils.TestUtils.addRedirectPageBypassSymbol;

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

        public static class LongURLInput {
            private static final String INPUT_SELECTOR = "#longUrlInput";
            public static final SelenideElement LABEL = TextFieldElement.byCss(INPUT_SELECTOR).getLabel();
            public static final SelenideElement INPUT = TextFieldElement.byCss(INPUT_SELECTOR).getInput();
            public static final SelenideElement CLEAR_BUTTON = TextFieldElement.byCss(INPUT_SELECTOR).getClearButton();
        }

        public static class ProtocolSelector {
            public static final SelenideElement SELECTOR = $("#protocolSelector");
            public static final SelenideElement LABEL = $("#label-vaadin-radio-group-3");
            public static final SelenideElement ERROR_MESSAGE = $("#error-message-vaadin-radio-group-5");
            public static final ElementsCollection OPTIONS = SELECTOR.$$("vaadin-radio-button");
            public static final SelenideElement HTTPS_OPTION =
                    $x("//vaadin-radio-button[.//*[@value='1']]");
            public static final SelenideElement HTTP_OPTION =
                    $x("//vaadin-radio-button[.//*[@value='2']]");
            public static final SelenideElement FTP_OPTION =
                    $x("//vaadin-radio-button[.//*[@value='3']]");
        }

        public static final SelenideElement DESCRIPTION_ACCORDION =
                $("#" + HomePage.IDs.DESCRIPTION_ACCORDION);

        public static class DescriptionInput {
            private static final String DESCRIPTION_INPUT_SELECTOR = "#descriptionInput";
            public static final SelenideElement ELEMENT = $(DESCRIPTION_INPUT_SELECTOR);
            public static final SelenideElement INPUT = TextFieldElement.byCss(DESCRIPTION_INPUT_SELECTOR).getInput();
            public static final SelenideElement CLEAR_BUTTON =
                    TextFieldElement.byCss(DESCRIPTION_INPUT_SELECTOR).getClearButton();

        }


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
                $("vaadin-notification-card");
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
        MainArea.LongURLInput.INPUT.setValue(link);
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
        MainArea.DescriptionInput.INPUT.setValue(description);

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

    /**
     * Cleans input field.
     */
    public static void cleanInput() {
        MainArea.LongURLInput.INPUT.setValue("");
    }

    /**
     * Pastes given description string to description field. Method opens accordion if it is closed.
     *
     * @param description not empty string with description.
     */
    public static void fillInDescription(final String description) {
        if (!MainArea.DescriptionInput.INPUT.isDisplayed()) {
            MainArea.DESCRIPTION_ACCORDION.click();
        }
        MainArea.DescriptionInput.INPUT.setValue(description);
    }

    /**
     * Cleans description input. Method opens accordion if it is closed.
     */
    public static void cleanDescription() {
        if (!MainArea.DescriptionInput.INPUT.isDisplayed()) {
            MainArea.DESCRIPTION_ACCORDION.click();
        }
        MainArea.DescriptionInput.INPUT.setValue("");
    }
}
