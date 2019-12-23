package eu.yals.test.ui.vaadin.tech;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.vaadin.testbench.TestBenchElement;
import org.openqa.selenium.WebElement;

public class PocObject {
    private TestBenchElement testBenchElement;
    private WebElement webElement;
    private SelenideElement selenideElement;


    public static PocObject fromTestBenchElement(TestBenchElement testBenchElement) {
        return new PocObject(testBenchElement);
    }

    private PocObject(TestBenchElement testBenchElement) {
        this.testBenchElement = testBenchElement;
        this.webElement = testBenchElement.getWrappedElement();
        this.selenideElement = Selenide.$(this.webElement);
    }

    public TestBenchElement asTestBenchElement() {
        return this.testBenchElement;
    }

    public SelenideElement asSelenideElement() {
        return this.selenideElement;
    }
}
