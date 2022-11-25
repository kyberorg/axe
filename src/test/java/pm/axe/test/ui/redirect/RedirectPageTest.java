package pm.axe.test.ui.redirect;

import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.RedirectPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.pageobjects.external.GitHub;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.redirect.RedirectPage;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;

/**
 * Testing {@link RedirectPage}.
 *
 * @since 3.0.5
 */
public class RedirectPageTest extends SelenideTest {

    private String ourShortLink;

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        if (Strings.isBlank(ourShortLink)) {
            open("/");
            VaadinPageObject.waitForVaadin();

            String ourLongLink = "https://github.com/kyberorg/axe/issues/353";
            ourShortLink = HomePageObject.storeAndReturnSavedUrl(ourLongLink);
            VaadinPageObject.waitForVaadin();
        }
        open(ourShortLink);
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Tests that on click on short link opens target page.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/832")
    public void shortLinkLeadsToLongLink() {
        RedirectPageObject.Links.ORIGIN_LINK.click();
        verifyThatGitHubOpened();
    }

    /**
     * Tests that on click long link to target page is opened.
     */
    @Test
    public void longLinkLeadsToTargetPage() {
        RedirectPageObject.Links.TARGET_LINK.click();
        verifyThatGitHubOpened();
    }

    /**
     * Tests that on click here link to target page is opened.
     */
    @Test
    public void hereLinkLeadsToTargetPage() {
        RedirectPageObject.Links.HERE_LINK.click();
        verifyThatGitHubOpened();
    }

    private void verifyThatGitHubOpened() {
        GitHub.GITHUB_HEADER.shouldBe(visible);
    }
}
