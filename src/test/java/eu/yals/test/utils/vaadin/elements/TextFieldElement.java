package eu.yals.test.utils.vaadin.elements;

import com.codeborne.selenide.SelenideElement;

import static eu.yals.test.utils.vaadin.VaadinUtils.$vaadin;

public class TextFieldElement extends VaadinElement {
    String tag = "vaadin-text-field";

    public static TextFieldElement byCss(String cssSelector) {
        TextFieldElement element = new TextFieldElement();
        element.cssSelector = cssSelector;
        return element;
    }

    @Override
    public String getTag() {
        return tag;
    }

    public SelenideElement getInput() {
        return $vaadin(cssSelector).shadowRoot("div.vaadin-text-field-container #vaadin-text-field-input-0 slot input");
    }
}
