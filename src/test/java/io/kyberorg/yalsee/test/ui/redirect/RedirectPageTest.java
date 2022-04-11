package io.kyberorg.yalsee.test.ui.redirect;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.RedirectPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.pageobjects.external.GitHub;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.pages.redirect.RedirectPage;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

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
            waitForVaadin();
            CookieBannerPageObject.closeBannerIfAny();

            String ourLongLink = "https://github.com/kyberorg/yalsee/issues/353";
            ourShortLink = HomePageObject.storeAndReturnSavedUrl(ourLongLink);
            waitForVaadin();
            CookieBannerPageObject.closeBannerIfAny();
        }
        open(ourShortLink);
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
    }

    /**
     * Tests that on click on short link same page is opened.
     */
    @Test
    public void shortLinkLeadsToSamePage() {
        RedirectPageObject.Links.ORIGIN_LINK.click();
        waitForVaadin();
        RedirectPageObject.VIEW.should(exist);
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
