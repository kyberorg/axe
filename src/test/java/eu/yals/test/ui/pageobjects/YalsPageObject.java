package eu.yals.test.ui.pageobjects;

import com.vaadin.testbench.TestBenchTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

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


}
