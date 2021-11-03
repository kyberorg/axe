package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.components.CookieBanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing {@link CookieBanner} behavior by performing actions.
 */
public class CookieBannerActionTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/");
        waitForVaadin();
        if (isBannerHidden()) {
            // resetting session
            open("/myLinks");
            waitForVaadin();
            if (isBannerDisplayed()) {
                //ready to test
                return;
            }
            MyLinksViewPageObject.cleanSession();
            waitForVaadin();
            if (isBannerHidden()) {
                fail("Cookie Banner didn't re-appear event after session restart");
            }
        }
    }

    @Test
    public void cookieBannerShownOncePerSession() {
        closeBannerIfAny();
        open("/");
        waitForVaadin();
        TITLE.shouldBe(hidden);
    }
}
