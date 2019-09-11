package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import ee.yals.utils.git.GitInfo;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.selectors.FrontSelectors.Footer.*;
import static junit.framework.Assert.assertNotNull;

/**
 * Checking elements of footer with information about version
 *
 * @since 2.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VersionInfoFooterTest {

    private GitInfo gitInfo = GitInfo.getInstance();

    @BeforeClass
    public static void setUp() {
        UITest.setUp();
    }

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void footerDisplayedOnlyWhenTagAndVersionArePresent() {
        boolean shouldCommitInfoDisplayed = shouldCommitInfoBeDisplayed();

        if (shouldCommitInfoDisplayed) {
            FOOTER.shouldBe(visible);
        } else {
            FOOTER.shouldNotBe(visible);
        }
    }

    @Test
    public void footerHasAllRequiredElements() {
        if (shouldCommitInfoBeDisplayed()) {
            VERSION.shouldBe(visible);
            VERSION.shouldHave(text("version")).shouldHave(text("commit"));
            COMMIT_LINK.shouldBe(visible);
            COMMIT_LINK.shouldNotBe(empty);
            COMMIT_LINK.shouldHave(attribute("href"));
        }
    }

    private boolean shouldCommitInfoBeDisplayed() {
        assertNotNull(this.gitInfo);

        String latestCommit = gitInfo.getLatestCommitHash().trim();
        String latestTag = gitInfo.getLatestTag().trim();

        boolean commitPresent = (!latestCommit.equals(GitInfo.NOTHING_FOUND_MARKER));
        boolean tagPresent = (!latestTag.equals(GitInfo.NOTHING_FOUND_MARKER));

        return commitPresent && tagPresent;
    }

    @AfterClass
    public static void tearDown() {
        UITest.tearDown();
    }
}
