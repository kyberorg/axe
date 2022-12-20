package pm.axe.test.ui.mylinks;

import com.codeborne.selenide.CollectionCondition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;

import static com.codeborne.selenide.Selenide.open;
import static pm.axe.Axe.C.FOUR;
import static pm.axe.Axe.C.THREE;

/**
 * Tests {@link MyLinksViewPageObject.ToggleColumnsMenu}.
 */
public class ToggleColumnsMenuTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
        MyLinksViewPageObject.cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.
    }

    /**
     * Refreshing page to clean state up.
     */
    @BeforeEach
    public void beforeEachTest() {
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Menu item should hide column.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void whenClickingOnItem_gridColumnsHides() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.LINK_ITEM.click();
        MyLinksViewPageObject.Grid.Header.get().getCells().shouldHave(CollectionCondition.size(THREE));
    }

    /**
     * 2 items clicked - 2 columns hidden.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void whenClickingOnTwoItems_twoColumnsAreHidden() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.LINK_ITEM.click();
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.DESCRIPTION_ITEM.click();
        MyLinksViewPageObject.Grid.Header.get().getCells().shouldHave(CollectionCondition.size(2));
    }

    /**
     * Menu item second click - re-enables grid column.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void whenClickItemAgain_columnIsBack() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.LINK_ITEM.click();
        MyLinksViewPageObject.Grid.Header.get().getCells().shouldHave(CollectionCondition.size(THREE));
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.LINK_ITEM.click();
        MyLinksViewPageObject.Grid.Header.get().getCells().shouldHave(CollectionCondition.size(FOUR));
    }
}
