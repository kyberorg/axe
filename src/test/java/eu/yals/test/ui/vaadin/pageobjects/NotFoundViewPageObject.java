package eu.yals.test.ui.vaadin.pageobjects;


import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import eu.yals.ui.err.NotFoundView;
import org.openqa.selenium.WebDriver;

public class NotFoundViewPageObject extends YalsPageObject {

    public static NotFoundViewPageObject getPageObject(WebDriver driver) {
        return new NotFoundViewPageObject(driver);
    }

    public NotFoundViewPageObject(WebDriver driver) {
        super(driver, NotFoundView.IDs.VIEW_ID);
    }

    public String getTitleText() {
        return getTitle().getText();
    }

    //elements
    public H1Element getTitle() {
        return $(H1Element.class).first();
    }

    public SpanElement getSubtitle() {
        return $(SpanElement.class).first();
    }

    public ImageElement getImage() {
        return $(ImageElement.class).first();
    }
}
