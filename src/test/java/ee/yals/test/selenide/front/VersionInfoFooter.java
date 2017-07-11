package ee.yals.test.selenide.front;

import ee.yals.utils.GitInfo;
import ee.yals.test.selenide.UITest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static junit.framework.Assert.assertNotNull;

/**
 * Checking elements of footer with information about version
 *
 * @since 2.0
 */
public class VersionInfoFooter extends UITest {

    @Autowired
    private GitInfo gitInfoBean;

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
        assertNotNull(this.gitInfoBean);

        String latestCommit = gitInfoBean.getLatestCommitHash();
        String latestTag = gitInfoBean.getLatestTag();

        return StringUtils.isNoneBlank(latestCommit, latestTag);
    }
}
