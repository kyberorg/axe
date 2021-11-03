package io.kyberorg.yalsee.test.ui.main;

import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.components.CookieBanner;
import io.kyberorg.yalsee.ui.dev.AppInfoView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.constants.App.THREE;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
     * Cookie Banner title has word Cookie inside.
     */
    @Test
    public void cookieBannerTitleHasCookieWord() {
        TITLE.shouldHave(text("Cookie"));
    }

    /**
     * Cookie Banner title should be H3 element.
     */
    @Test
    public void cookieBannerTitleIsH3() {
        assertEquals("h3", TITLE.getTagName());
    }

    /**
     * Cookie Banner should have text visible.
     */
    @Test
    public void cookieBannerHasText() {
        BannerText.TEXT.should(exist);
        BannerText.TEXT.shouldBe(visible);
    }

    /**
     * Cookie Banner should have needed words inside.
     */
    @Test
    public void cookieBannerHasNeededWords() {
        BannerText.TEXT.shouldHave(text("website"));
        BannerText.TEXT.shouldHave(text("cookies"));
        BannerText.TEXT.shouldHave(text("works"));
        BannerText.TEXT.shouldHave(text("shit"));
    }

    /**
     * Cookie Banner should have More Info Link.
     */
    @Test
    public void cookieBannerHasMoreInfoLink() {
        BannerText.LINK.should(exist);
        BannerText.LINK.shouldBe(visible);

        BannerText.LINK.shouldHave(text("More Info"));
    }

    /**
     * More Info Link links with {@link AppInfoView} Page.
     */
    @Test
    public void moreInfoLinkHrefIsAppInfoPage() {
        BannerText.LINK.shouldHave(attributeMatching("href", "appInfo"));
    }

    /**
     * Cookie Banner should have 2 boxes.
     */
    @Test
    public void cookieBannerShouldHaveTwoBoxes() {
        Boxes.BOXES.shouldHave(size(2));
    }

    /**
     * Technical Box should be visible, but disabled.
     */
    @Test
    public void technicalBoxShouldBeVisibleButDisabled() {
        Boxes.ONLY_NECESSARY_BOX.should(exist);
        Boxes.ONLY_NECESSARY_BOX.shouldBe(visible);
        Boxes.ONLY_NECESSARY_BOX.shouldHave(attribute("disabled"));
    }

    /**
     * Technical Box should have correct Text.
     */
    @Test
    public void technicalBoxShouldHaveCorrectText() {
        Boxes.ONLY_NECESSARY_BOX.shouldHave(text("Technical"));
    }

    /**
     * Analytics Box should be visible and enabled.
     */
    @Test
    public void analyticsBoxShouldBeVisibleAndEnabled() {
        Boxes.ANALYTICS_BOX.should(exist);
        Boxes.ANALYTICS_BOX.shouldBe(visible);
        Boxes.ANALYTICS_BOX.shouldBe(enabled);
    }

    /**
     * Analytics Box should have correct Text.
     */
    @Test
    public void analyticsBoxShouldHaveCorrectText() {
        Boxes.ANALYTICS_BOX.shouldHave(text("Analytics"));
    }

    /**
     * Cookie Banner should have 3 buttons.
     */
    @Test
    public void cookieBannerShouldHaveThreeButtons() {
        Buttons.BUTTONS.shouldHave(size(THREE));
    }

    /**
     * Buttons should be small.
     */
    @Test
    public void buttonsShouldBeSmall() {
        Buttons.BUTTONS.filterBy(attributeMatching("theme", ".*small*"))
                .shouldHave(size(THREE));
    }

    /**
     * Only Necessary Button should be visible and enabled.
     */
    @Test
    public void onlyNecessaryButtonShouldBeVisibleAndEnabled() {
        Buttons.ONLY_NECESSARY_BUTTON.should(exist);
        Buttons.ONLY_NECESSARY_BUTTON.shouldBe(visible);
        Buttons.ONLY_NECESSARY_BUTTON.shouldBe(enabled);
    }

    /**
     * Only Necessary Button should have correct Text.
     */
    @Test
    public void onlyNecessaryButtonShouldHaveCorrectText() {
        Buttons.ONLY_NECESSARY_BUTTON.shouldHave(text("Only necessary cookies"));
    }

    /**
     * Only Necessary Button should be primary and contrast.
     * See {@linkplain <a href="https://yls.ee/LOomqY+">Doks</a>} for more info.
     */
    @Test
    public void onlyNecessaryButtonShouldBePrimaryAndContrast() {
        Buttons.ONLY_NECESSARY_BUTTON.shouldHave(attributeMatching("theme", ".*primary*"));
        Buttons.ONLY_NECESSARY_BUTTON.shouldHave(attributeMatching("theme", ".*contrast*"));
    }

    /**
     * Selection Button should be visible and enabled.
     */
    @Test
    public void selectionButtonShouldBeVisibleAndEnabled() {
        Buttons.SELECTION_BUTTON.should(exist);
        Buttons.SELECTION_BUTTON.shouldBe(visible);
        Buttons.SELECTION_BUTTON.shouldBe(enabled);
    }

    /**
     * Selection Button should have correct Text.
     */
    @Test
    public void selectionButtonShouldHaveCorrectText() {
        Buttons.SELECTION_BUTTON.shouldHave(text("Allow selection"));
    }

    /**
     * Selection Button should be primary.
     */
    @Test
    public void selectionButtonShouldBePrimary() {
        Buttons.SELECTION_BUTTON.shouldHave(attributeMatching("theme", ".*primary*"));
    }

    /**
     * Allow All Button should be visible and enabled.
     */
    @Test
    public void allowAllButtonShouldBeVisibleAndEnabled() {
        Buttons.ALLOW_ALL_BUTTON.should(exist);
        Buttons.ALLOW_ALL_BUTTON.shouldBe(visible);
        Buttons.ALLOW_ALL_BUTTON.shouldBe(enabled);
    }

    /**
     * Allow All Button should have correct Text.
     */
    @Test
    public void allowAllButtonShouldHaveCorrectText() {
        Buttons.ALLOW_ALL_BUTTON.shouldHave(text("Allow all cookies"));
    }

    /**
     * Allow All Button should be primary.
     */
    @Test
    public void allowAllButtonShouldBePrimary() {
        Buttons.ALLOW_ALL_BUTTON.shouldHave(attributeMatching("theme", ".*primary*"));
    }
}
