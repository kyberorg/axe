package io.kyberorg.yalsee.test.utils.vaadin.elements;

import com.codeborne.selenide.SelenideElement;

import static io.kyberorg.yalsee.test.utils.vaadin.VaadinUtils.$vaadin;

public class TextFieldElement {
    private String cssSelector;

    public static TextFieldElement byCss(String cssSelector) {
        TextFieldElement element = new TextFieldElement();
        element.cssSelector = cssSelector;
        return element;
    }

    public SelenideElement getInput() {
        return $vaadin(cssSelector).shadowRoot("div.vaadin-text-field-container #vaadin-text-field-input-0 slot input");
    }

    public SelenideElement getLabel() {
        return $vaadin(cssSelector).shadowRoot("div.vaadin-text-field-container label");
    }
}
