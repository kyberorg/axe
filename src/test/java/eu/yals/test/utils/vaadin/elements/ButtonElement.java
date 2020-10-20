package eu.yals.test.utils.vaadin.elements;

import com.codeborne.selenide.SelenideElement;

import static eu.yals.test.utils.vaadin.VaadinUtils.$vaadin;

public class ButtonElement extends VaadinElement {
    String tag = "vaadin-button";

    public static ButtonElement byCss(String cssSelector) {
        ButtonElement element = new ButtonElement();
        element.cssSelector = cssSelector;
        return element;
    }

    @Override
    public String getTag() {
        return tag;
    }

    public SelenideElement getButton() {
        return $vaadin(cssSelector).shadowRoot("#button");
    }
}
