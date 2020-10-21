package eu.yals.test.ui.pageobjects.uus;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.utils.vaadin.elements.ButtonElement;
import eu.yals.test.utils.vaadin.elements.TextFieldElement;
import eu.yals.ui.HomeView;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class HomePageObject {

    public static class MainArea {
        public static final SelenideElement LONG_URL_INPUT = TextFieldElement.byCss("#" + HomeView.IDs.INPUT).getInput();
        public static final SelenideElement SUBMIT_BUTTON = ButtonElement.byCss("#" + HomeView.IDs.SUBMIT_BUTTON).getButton();
    }

    public static class OverallArea {

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

    public static class ErrorModal {
        public static final SelenideElement ERROR_MODAL = $("#vaadin-notification-card");
        public static final SelenideElement ERROR_TEXT = $("#vaadin-notification-card");
    }

    public static void pasteValueInForm(String link) {
        MainArea.LONG_URL_INPUT.setValue(link);
    }


    public static void pasteValueInFormAndSubmitIt(String link) {
        pasteValueInForm(link);
        MainArea.SUBMIT_BUTTON.click();
    }

    public static void storeAndOpenSavedUrl(String urlToStore) {
        pasteValueInFormAndSubmitIt(urlToStore);
        String shortLink = HomePageObject.ResultArea.RESULT_LINK.getText();
        open(shortLink);
    }


}
