package ee.yals.test.selenide.front;

import ee.yals.test.selenide.UITest;
import ee.yals.utils.git.GitInfo;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
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
            $("footer").shouldBe(visible);
        } else {
            $("footer").shouldNotBe(visible);
        }
    }

    @Test
    public void footerHasAllRequiredElements() {
        if(shouldCommitInfoBeDisplayed()){
            $("#version").shouldBe(visible);
            $("#version").shouldHave(text("version")).shouldHave(text("commit"));
            $("#version a").shouldBe(visible);
            $("#version a").shouldNotBe(empty);
            $("#version a").shouldHave(attribute("href"));
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
