package eu.yals.test.utils.vaadin.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import lombok.Setter;

import static com.codeborne.selenide.Selenide.$;

public abstract class VaadinElement {
    @Setter
    protected String tag;
    @Getter
    @Setter
    protected String cssSelector;

    protected VaadinElement() {
        cssSelector = getTag();
    }

    public abstract String getTag();

    public SelenideElement getSelf() {
        return $(cssSelector);
    }

}
