package eu.yals.test.selenide.front;

import eu.yals.services.GitService;
import eu.yals.test.selenide.UITest;
import eu.yals.test.utils.pages.FrontPage;
import eu.yals.utils.git.NoGitInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

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
            FrontPage.Footer.FOOTER.shouldNotBe(visible);
        } else {
            FrontPage.Footer.FOOTER.shouldBe(visible);
        }
    }

    @Test
    public void footerHasAllRequiredElements() {
        if (isFooterVisible()) {
            FrontPage.Footer.VERSION.shouldBe(visible);
            FrontPage.Footer.VERSION.shouldHave(text("version")).shouldHave(text("commit"));
            FrontPage.Footer.COMMIT_LINK.shouldBe(visible);
            FrontPage.Footer.COMMIT_LINK.shouldNotBe(empty);
            FrontPage.Footer.COMMIT_LINK.shouldHave(attribute("href"));
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
