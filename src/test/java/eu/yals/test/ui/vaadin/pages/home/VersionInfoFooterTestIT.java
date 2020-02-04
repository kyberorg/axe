package eu.yals.test.ui.vaadin.pages.home;

import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.vaadin.HomePageTest;
import org.junit.Test;

/**
 * Checking elements of footer with information about version
 *
 * @since 2.0
 */
public class VersionInfoFooterTestIT extends HomePageTest {

    @Test
    public void footerHasAllRequiredElements() {
        openHomePage();
        if(isFooterNotVisible()) {
            //footer is not visible, when there is no git info aka local run
            return;
        }
        $$(homeView.getVersion()).shouldBeDisplayed();
        $$(homeView.getVersion()).shouldHaveText("version");
        $$(homeView.getVersion()).shouldHaveText("commit");
        $$(homeView.getCommitLink()).shouldBeDisplayed();
        $$(homeView.getCommitLink()).shouldNotBeEmpty();
        $$(homeView.getCommitLink()).shouldHaveAttr("href");
    }

    private boolean isFooterNotVisible() {
        TestBenchElement footer = homeView.getFooter();
        return ! footer.isDisplayed();
    }
}
