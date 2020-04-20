package eu.yals.test.ui.appinfo;

import eu.yals.services.GitService;
import eu.yals.test.ui.AppInfoPageTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checking elements of public info area with information about version
 *
 * @since 2.7
 */
@Component
public class PublicInfoTestIT extends AppInfoPageTest {

    @Autowired
    private GitService gitService;

    @Test
    public void publicAreaHasAllRequiredElements() {
        openPage();
        if (gitService.tagPresent() && gitService.commitPresent()) {
            //no git info aka local run
            return;
        }

        if (gitService.tagPresent()) {
            $$(appInfoView.getVersion()).shouldBeDisplayed();
            $$(appInfoView.getVersion()).shouldHaveText("version");
            $$(appInfoView.getVersion()).shouldHaveText("commit");

            if (gitService.commitPresent()) {
                $$(appInfoView.getCommitLink()).shouldBeDisplayed();
                $$(appInfoView.getCommitLink()).shouldNotBeEmpty();
                $$(appInfoView.getCommitLink()).shouldHaveAttr("href");
            }
        }
    }

}
