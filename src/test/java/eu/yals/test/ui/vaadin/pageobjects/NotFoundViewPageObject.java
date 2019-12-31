package eu.yals.test.ui.vaadin.pageobjects;


import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import eu.yals.ui.err.NotFoundView;
import org.openqa.selenium.WebDriver;

public class NotFoundViewPageObject extends YalsPageObject {

    public H1Element TITLE = $(H1Element.class).first();
    public SpanElement SUBTITLE = $(SpanElement.class).first();
    public ImageElement IMAGE = $(ImageElement.class).first();

    public static NotFoundViewPageObject getPageObject(WebDriver driver) {
        return new NotFoundViewPageObject(driver);
    }

    public NotFoundViewPageObject(WebDriver driver) {
        super(driver, NotFoundView.IDs.VIEW_ID);
    }

    public String getTitleText() {
        return TITLE.getText();
    }

}
