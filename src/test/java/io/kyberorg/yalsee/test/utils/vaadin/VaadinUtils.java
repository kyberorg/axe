package io.kyberorg.yalsee.test.utils.vaadin;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.shadowCss;
import static com.codeborne.selenide.Selenide.$;

public final class VaadinUtils {

    private final String shadowHost;

    private VaadinUtils(final String shadowHost) {
        this.shadowHost = shadowHost;
    }

    /**
     * Vaadin adapted version of {@link com.codeborne.selenide.Selenide#$(String)}.
     *
     * @param elementSelector string with CSS JQuery-styled selector.
     * @return created {@link VaadinUtils} object to later usage.
     */
    public static VaadinUtils $vaadin(final String elementSelector) {
        return new VaadinUtils(elementSelector);
    }

    /**
     * Gets element from inside of shadowRoot of Vaadin element.
     *
     * @param selectorInsideShadowRoot string with CSS JQuery-styled selector.
     * @return found {@link SelenideElement}.
     */
    public SelenideElement shadowRoot(final String selectorInsideShadowRoot) {
        return $(shadowCss(selectorInsideShadowRoot, this.shadowHost));
    }

}
