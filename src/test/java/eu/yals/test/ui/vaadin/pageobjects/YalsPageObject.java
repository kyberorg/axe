package eu.yals.test.ui.vaadin.pageobjects;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.TestBenchTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class YalsPageObject extends TestBenchTestCase {

    private final SearchContext context;

    public YalsPageObject(WebDriver driver, SearchContext context) {
        setDriver(driver);
        this.context = context;
    }

    public YalsPageObject(WebDriver driver, String contextId) {
        this(driver, driver.findElement(By.id(contextId)));
    }

    @Override
    public SearchContext getContext() {
        return context;
    }

    public SelenideElement se(TestBenchElement testBenchElement) {
        WebElement webElement = testBenchElement.getWrappedElement();
        return Selenide.$(webElement);
    }
}
