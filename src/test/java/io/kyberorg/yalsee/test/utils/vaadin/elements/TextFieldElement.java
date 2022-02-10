package io.kyberorg.yalsee.test.utils.vaadin.elements;

import com.codeborne.selenide.SelenideElement;
import com.vaadin.flow.component.textfield.TextField;

import static io.kyberorg.yalsee.test.utils.vaadin.VaadinUtils.$vaadin;

/**
 * Methods for Vaadin's {@link TextField}.
 *
 * @since 2.7
 */
public class TextFieldElement {
    private String cssSelector;

    /**
     * Provides element object by its CSS selector. This method doesn't start any search.
     *
     * @param cssSelector string with valid CSS JQuery-styled selector. Ex. #myId or .myClass
     * @return created {@link TextFieldElement}
     */
    public static TextFieldElement byCss(final String cssSelector) {
        TextFieldElement element = new TextFieldElement();
        element.cssSelector = cssSelector;
        return element;
    }

    /**
     * Provides input element of Vaadin's {@link TextField}.
     *
     * @return {@link SelenideElement}, which represents input element.
     */
    public SelenideElement getInput() {
        return $vaadin(cssSelector).shadowRoot("div.vaadin-text-field-container div[part=\"input-field\"] slot input");
    }

    /**
     * Provides label element of Vaadin's {@link TextField}.
     *
     * @return {@link SelenideElement}, which represents label element.
     */
    public SelenideElement getLabel() {
        return $vaadin(cssSelector).shadowRoot("div.vaadin-text-field-container label");
    }

    public SelenideElement getCleanButton() {
        return $vaadin(cssSelector).shadowRoot("div.vaadin-text-field-container #clearButton");
    }
}
