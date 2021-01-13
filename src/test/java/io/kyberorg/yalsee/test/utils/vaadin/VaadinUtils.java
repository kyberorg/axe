package io.kyberorg.yalsee.test.utils.vaadin;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.shadowCss;
import static com.codeborne.selenide.Selenide.$;

public class VaadinUtils {

    private final String shadowHost;

    private VaadinUtils(String shadowHost) {
        this.shadowHost = shadowHost;
    }

    public static VaadinUtils $vaadin(String elementSelector) {
        return new VaadinUtils(elementSelector);
    }

    public SelenideElement shadowRoot(String selectorInsideShadowRoot) {
        return $(shadowCss(selectorInsideShadowRoot, this.shadowHost));
    }

}
