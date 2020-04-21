package eu.yals.test.ui.appinfo;

import eu.yals.test.ui.AppInfoPageTest;
import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * Checking elements of public info area with information about version
 *
 * @since 2.7
 */
@Component
public class PublicInfoTestIT extends AppInfoPageTest {

    @Test
    public void publicAreaHasAllRequiredElements() {
        openPage();

        $$(appInfoView.getVersion()).shouldBeDisplayed();
        $$(appInfoView.getVersion()).shouldHaveText("version");
        $$(appInfoView.getVersion()).shouldHaveText("commit");

        $$(appInfoView.getCommitLink()).shouldBeDisplayed();
        $$(appInfoView.getCommitLink()).shouldNotBeEmpty();
        $$(appInfoView.getCommitLink()).shouldHaveAttr("href");
    }

}
