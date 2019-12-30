package eu.yals.test.ui.vaadin.tech;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.vaadin.testbench.TestBenchElement;
import org.openqa.selenium.WebElement;

public class ElementConverter {

    public static ElementConverter get() {
        return new ElementConverter();
    }

    public SelenideElement convert(TestBenchElement testBenchElement) {
        WebElement webElement = testBenchElement.getWrappedElement();
        return Selenide.$(webElement);
    }
}
