package eu.yals.test.ui.pageobjects;

import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.TestBenchElement;
import eu.yals.ui.dev.AppInfoView;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for {@link AppInfoView}.
 *
 * @since 2.7
 */
public class AppInfoPageObject extends YalsPageObject {

    public static AppInfoPageObject getPageObject(WebDriver driver) {
        return new AppInfoPageObject(driver);
    }
    public AppInfoPageObject(WebDriver driver) {
        super(driver, AppInfoView.Id.VIEW_ID);
    }

    public TestBenchElement getPublicInfoArea() {
        return $(TestBenchElement.class).id(AppInfoView.Id.PUBLIC_INFO_AREA);
    }

    public SpanElement getVersion() {
        return getPublicInfoArea().$(SpanElement.class).id(AppInfoView.Id.VERSION);
    }

    public AnchorElement getCommitLink() {
        return getPublicInfoArea().$(AnchorElement.class).id(AppInfoView.Id.COMMIT_LINK);
    }
}
