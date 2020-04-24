package eu.yals.test.ui.pageobjects;

import com.vaadin.flow.component.html.testbench.H1Element;
import eu.yals.ui.err.NotFoundView;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for {@link NotFoundView}. Contains elements from NotFoundView
 *
 * @since 2.7
 */
public class NotFoundViewPageObject extends YalsPageObject {

    public static NotFoundViewPageObject getPageObject(WebDriver driver) {
        return new NotFoundViewPageObject(driver);
    }

    public NotFoundViewPageObject(WebDriver driver) {
        super(driver, NotFoundView.IDs.VIEW_ID);
    }

    // elements
    public H1Element getTitle() {
        return $(H1Element.class).first();
    }
}
