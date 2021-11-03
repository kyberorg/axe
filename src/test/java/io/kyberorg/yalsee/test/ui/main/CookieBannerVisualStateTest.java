package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.components.CookieBanner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing {@link CookieBanner}'s visual state.
 *
 * @since 3.5
 */
@Execution(ExecutionMode.CONCURRENT)
public class CookieBannerVisualStateTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
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

    /**
     * Cookie Banner has title.
     */
    @Test
    public void cookieBannerHasTitle() {
        TITLE.should(exist);
        TITLE.shouldBe(visible);
    }

    /**
     * Cookie Banner title has word Cookie inside.s
     */
    @Test
    public void cookieBannerTitleHasCookieWord() {
        TITLE.shouldHave(text("Cookie"));
    }
}
