package pm.axe.test.ui.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import pm.axe.ui.elements.CookieBanner;
import pm.axe.ui.pages.appinfo.AppInfoPage;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.fail;
import static pm.axe.Axe.C.THREE;

/**
 * Testing {@link CookieBanner}'s visual state.
 *
 * @since 3.5
 */
@Disabled("Since we don't use CookieBanner anymore - no testing needed")
public class CookieBannerVisualStateTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/");
        VaadinPageObject.waitForVaadin();
        if (CookieBannerPageObject.isBannerHidden()) {
            // resetting session
            open("/myLinks");
            VaadinPageObject.waitForVaadin();
            if (CookieBannerPageObject.isBannerDisplayed()) {
                //ready to test
                return;
            }
            MyLinksViewPageObject.cleanSession();
            VaadinPageObject.waitForVaadin();
            if (CookieBannerPageObject.isBannerHidden()) {
                fail("Cookie Banner didn't re-appear event after session restart");
            }
        }
    }

    /**
     * Cookie Banner has title.
     */
    @Test
    public void cookieBannerHasTitle() {
        CookieBannerPageObject.TITLE.should(exist);
        CookieBannerPageObject.TITLE.shouldBe(visible);
    }

    /**
     * Cookie Banner title has word Cookie inside.
     */
    @Test
    public void cookieBannerTitleHasCookieWord() {
        CookieBannerPageObject.TITLE.shouldHave(text("Cookie"));
    }

    /**
     * Cookie Banner title should be H3 element.
     */
    @Test
    public void cookieBannerTitleIsH3() {
        Assertions.assertEquals("h3", CookieBannerPageObject.TITLE.getTagName());
    }

    /**
     * Cookie Banner should have text visible.
     */
    @Test
    public void cookieBannerHasText() {
        CookieBannerPageObject.BannerText.TEXT.should(exist);
        CookieBannerPageObject.BannerText.TEXT.shouldBe(visible);
    }

    /**
     * Cookie Banner should have needed words inside.
     */
    @Test
    public void cookieBannerHasNeededWords() {
        CookieBannerPageObject.BannerText.TEXT.shouldHave(text("website"));
        CookieBannerPageObject.BannerText.TEXT.shouldHave(text("cookies"));
        CookieBannerPageObject.BannerText.TEXT.shouldHave(text("works"));
        CookieBannerPageObject.BannerText.TEXT.shouldHave(text("shit"));
    }

    /**
     * Cookie Banner should have More Info Link.
     */
    @Test
    public void cookieBannerHasMoreInfoLink() {
        CookieBannerPageObject.BannerText.LINK.should(exist);
        CookieBannerPageObject.BannerText.LINK.shouldBe(visible);

        CookieBannerPageObject.BannerText.LINK.shouldHave(text("More Info"));
    }

    /**
     * More Info Link links with {@link AppInfoPage} Page.
     */
    @Test
    public void moreInfoLinkHrefIsAppInfoPage() {
        CookieBannerPageObject.BannerText.LINK.shouldHave(attributeMatching("href",
                TestUtils.getTestUrl() + "/appInfo"));
    }

    /**
     * Cookie Banner should have 2 boxes.
     */
    @Test
    public void cookieBannerShouldHaveTwoBoxes() {
        CookieBannerPageObject.Boxes.BOXES.shouldHave(size(2));
    }

    /**
     * Technical Box should be visible, but disabled.
     */
    @Test
    public void technicalBoxShouldBeVisibleButDisabled() {
        CookieBannerPageObject.Boxes.ONLY_NECESSARY_BOX.should(exist);
        CookieBannerPageObject.Boxes.ONLY_NECESSARY_BOX.shouldBe(visible);
        CookieBannerPageObject.Boxes.ONLY_NECESSARY_BOX.shouldHave(attribute("disabled"));
    }

    /**
     * Technical Box should have correct Text.
     */
    @Test
    public void technicalBoxShouldHaveCorrectText() {
        CookieBannerPageObject.Boxes.ONLY_NECESSARY_BOX.shouldHave(text("Technical"));
    }

    /**
     * Analytics Box should be visible and enabled.
     */
    @Test
    public void analyticsBoxShouldBeVisibleAndEnabled() {
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.should(exist);
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.shouldBe(visible);
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.shouldBe(enabled);
    }

    /**
     * Analytics Box should have correct Text.
     */
    @Test
    public void analyticsBoxShouldHaveCorrectText() {
        CookieBannerPageObject.Boxes.ANALYTICS_BOX.shouldHave(text("Analytics"));
    }

    /**
     * Cookie Banner should have 3 buttons.
     */
    @Test
    public void cookieBannerShouldHaveThreeButtons() {
        CookieBannerPageObject.Buttons.BUTTONS.shouldHave(size(THREE));
    }

    /**
     * Buttons should be small.
     */
    @Test
    public void buttonsShouldBeSmall() {
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON, "small");
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.SELECTION_BUTTON, "small");
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON, "small");
    }

    /**
     * Only Necessary Button should be visible and enabled.
     */
    @Test
    public void onlyNecessaryButtonShouldBeVisibleAndEnabled() {
        CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON.should(exist);
        CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON.shouldBe(visible);
        CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON.shouldBe(enabled);
    }

    /**
     * Only Necessary Button should have correct Text.
     */
    @Test
    public void onlyNecessaryButtonShouldHaveCorrectText() {
        CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON.shouldHave(text("Only necessary cookies"));
    }

    /**
     * Only Necessary Button should be primary and contrast.
     * See {@linkplain <a href="https://axe.pm/LOomqY+">Doks</a>} for more info.
     */
    @Test
    public void onlyNecessaryButtonShouldBePrimaryAndContrast() {
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON, "primary");
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.ONLY_NECESSARY_BUTTON, "contrast");
    }

    /**
     * Selection Button should be visible and enabled.
     */
    @Test
    public void selectionButtonShouldBeVisibleAndEnabled() {
        CookieBannerPageObject.Buttons.SELECTION_BUTTON.should(exist);
        CookieBannerPageObject.Buttons.SELECTION_BUTTON.shouldBe(visible);
        CookieBannerPageObject.Buttons.SELECTION_BUTTON.shouldBe(enabled);
    }

    /**
     * Selection Button should have correct Text.
     */
    @Test
    public void selectionButtonShouldHaveCorrectText() {
        CookieBannerPageObject.Buttons.SELECTION_BUTTON.shouldHave(text("Allow selection"));
    }

    /**
     * Selection Button should be primary.
     */
    @Test
    public void selectionButtonShouldBePrimary() {
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.SELECTION_BUTTON, "primary");
    }

    /**
     * Allow All Button should be visible and enabled.
     */
    @Test
    public void allowAllButtonShouldBeVisibleAndEnabled() {
        CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.should(exist);
        CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.shouldBe(visible);
        CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.shouldBe(enabled);
    }

    /**
     * Allow All Button should have correct Text.
     */
    @Test
    public void allowAllButtonShouldHaveCorrectText() {
        CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON.shouldHave(text("Allow all cookies"));
    }

    /**
     * Allow All Button should be primary.
     */
    @Test
    public void allowAllButtonShouldBePrimary() {
        TestUtils.assertHasTheme(CookieBannerPageObject.Buttons.ALLOW_ALL_BUTTON, "primary");
    }
}
