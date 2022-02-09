package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.CollectionCondition;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.constants.App.FOUR;
import static io.kyberorg.yalsee.constants.App.THREE;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Tests {@link ToggleColumnsMenu}.
 */
public class ToggleColumnsMenuTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/myLinks");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        CookieBannerPageObject.closeBannerIfAny(); //banner re-appears for new session
    }

    @BeforeEach
    public void beforeEachTest() {
        //page refresh - to clean up menu state.
        open("/myLinks");
        waitForVaadin();
    }

    /**
     * Menu item should hide column.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void whenClickingOnItem_gridColumnsHides() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.LINK_ITEM.click();
        Grid.Header.get().getCells().shouldHave(CollectionCondition.size(THREE));
    }

    /**
     * 2 items clicked - 2 columns hidden.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void whenClickingOnTwoItems_twoColumnsAreHidden() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.LINK_ITEM.click();
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.DESCRIPTION_ITEM.click();
        Grid.Header.get().getCells().shouldHave(CollectionCondition.size(2));
    }

    /**
     * Menu item second click - re-enables grid column.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void whenClickItemAgain_columnIsBack() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.LINK_ITEM.click();
        Grid.Header.get().getCells().shouldHave(CollectionCondition.size(THREE));
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.LINK_ITEM.click();
        Grid.Header.get().getCells().shouldHave(CollectionCondition.size(FOUR));
    }
}
