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

    /**
     * Provides page object.
     *
     * @param driver web driver
     * @return created {@link AppInfoPageObject}
     */
    public static AppInfoPageObject getPageObject(final WebDriver driver) {
        return new AppInfoPageObject(driver);
    }

    /**
     * Creates {@link AppInfoPageObject}.
     *
     * @param driver web driver
     */
    public AppInfoPageObject(final WebDriver driver) {
        super(driver, AppInfoView.IDs.VIEW_ID);
    }

    /**
     * Public area.
     *
     * @return element
     */
    public TestBenchElement getPublicInfoArea() {
        return $(TestBenchElement.class).id(AppInfoView.IDs.PUBLIC_INFO_AREA);
    }

    /**
     * Version span.
     *
     * @return element
     */
    public SpanElement getVersion() {
        return getPublicInfoArea().$(SpanElement.class).id(AppInfoView.IDs.VERSION);
    }

    /**
     * Commit link.
     *
     * @return element
     */
    public AnchorElement getCommitLink() {
        return getPublicInfoArea().$(AnchorElement.class).id(AppInfoView.IDs.COMMIT_LINK);
    }
}
