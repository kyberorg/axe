package eu.yals.test.ui.pageobjects;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.utils.vaadin.elements.ButtonElement;
import eu.yals.test.utils.vaadin.elements.TextFieldElement;
import eu.yals.ui.HomeView;

import static com.codeborne.selenide.Selenide.$;

public class NewHomePageObject {

    public static class MainArea {
        public static final SelenideElement INPUT = TextFieldElement.byCss("#" + HomeView.IDs.INPUT).getInput();
        private static SelenideElement SUBMIT_BUTTON = ButtonElement.byCss("#" + HomeView.IDs.SUBMIT_BUTTON).getButton();
    }

    public static class OverallArea {

    }

    public static class ResultArea {
        public static final SelenideElement RESULT_LINK = $("#" + HomeView.IDs.SHORT_LINK);
    }

    public static class QrCodeArea {

    }


    public static void pasteValueInForm(String link) {
        MainArea.INPUT.setValue(link);
    }


    public static void pasteValueInFormAndSubmitIt(String link) {
        pasteValueInForm(link);
        MainArea.SUBMIT_BUTTON.click();
    }


}
