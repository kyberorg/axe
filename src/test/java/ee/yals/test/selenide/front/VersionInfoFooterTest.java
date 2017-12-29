package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import ee.yals.utils.git.GitInfo;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.selectors.FrontSelectors.Footer.*;
import static junit.framework.Assert.assertNotNull;

/**
 * Checking elements of footer with information about version
 *
 * @since 2.0
 */
public class VersionInfoFooterTest extends UITest {

    private GitInfo gitInfo = GitInfo.getInstance();

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
        if(shouldCommitInfoBeDisplayed()){
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
}
