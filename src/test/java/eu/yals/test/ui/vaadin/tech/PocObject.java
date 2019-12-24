package eu.yals.test.ui.vaadin.tech;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.vaadin.testbench.TestBenchElement;
import org.openqa.selenium.WebElement;

public class PocObject {
    private TestBenchElement testBenchElement;
    private SelenideElement selenideElement;


    public static PocObject fromTestBenchElement(TestBenchElement testBenchElement) {
        return new PocObject(testBenchElement);
    }

    private PocObject(TestBenchElement testBenchElement) {
        this.testBenchElement = testBenchElement;
        WebElement webElement = testBenchElement.getWrappedElement();
        this.selenideElement = Selenide.$(webElement);
    }

    public TestBenchElement asTBE() {
        return this.testBenchElement;
    }

    public SelenideElement asSE() {
        return this.selenideElement;
    }

    //like public H1Element as(H1Element.class) method
    public <T extends TestBenchElement> T as(Class<T> clazz) {
        return clazz.cast(this.testBenchElement);
    }
}
