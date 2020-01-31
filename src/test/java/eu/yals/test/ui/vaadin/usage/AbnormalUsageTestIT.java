package eu.yals.test.ui.vaadin.usage;

import com.vaadin.flow.component.html.testbench.AnchorElement;
import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import org.junit.Test;

/**
 * Tests unusual usage
 *
 * @since 2.0
 */
public class AbnormalUsageTestIT extends VaadinTest {

    @Test
    public void extraArgumentsShouldBeIgnored() {
        final String EXTRA_ARGUMENT = "mineMetsa";
        final String LINK_TO_SAVE = "https://vr.fi";

        open("/?" + EXTRA_ARGUMENT);
        HomeViewPageObject homeView = HomeViewPageObject.getPageObject(getDriver());
        homeView.pasteValueInFormAndSubmitIt(LINK_TO_SAVE);

        AnchorElement resultLink = homeView.getShortLink();
        $$(resultLink).shouldNotBeEmpty();
        $$(resultLink).shouldNotHaveText(EXTRA_ARGUMENT);
    }
}
