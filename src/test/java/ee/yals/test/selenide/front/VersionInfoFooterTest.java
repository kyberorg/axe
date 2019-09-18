package ee.yals.test.selenide.front;

import ee.yals.services.GitService;
import ee.yals.test.selenide.UITest;
import ee.yals.utils.git.NoGitInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.pages.FrontPage.Footer.*;

/**
 * Checking elements of footer with information about version
 *
 * @since 2.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VersionInfoFooterTest extends UITest {

    @Autowired
    private GitService gitService;

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void footerDisplayedOnlyWhenTagAndVersionArePresent() {
        if (isLocalRunWithoutMaven()) {
            FOOTER.shouldNotBe(visible);
        } else {
            FOOTER.shouldBe(visible);
        }
    }

    @Test
    public void footerHasAllRequiredElements() {
        if (isFooterVisible()) {
            VERSION.shouldBe(visible);
            VERSION.shouldHave(text("version")).shouldHave(text("commit"));
            COMMIT_LINK.shouldBe(visible);
            COMMIT_LINK.shouldNotBe(empty);
            COMMIT_LINK.shouldHave(attribute("href"));
        }
    }

    private boolean isFooterVisible() {
        return !isLocalRunWithoutMaven();
    }

    private boolean isLocalRunWithoutMaven() {
        //indicates than maven did not run and therefore generated nothing about git
        return (gitService.getGitInfoSource() instanceof NoGitInfo);
    }
}
