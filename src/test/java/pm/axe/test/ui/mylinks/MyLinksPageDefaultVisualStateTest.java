package pm.axe.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.DebugPageObject;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import pm.axe.test.utils.vaadin.elements.TextFieldElement;
import pm.axe.ui.pages.home.HomePage;
import pm.axe.ui.pages.mylinks.MyLinksPage;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static pm.axe.Axe.C.FOUR;

/**
 * Test default visual state of {@link MyLinksPage}. This state, when user just opens page without doing something else.
 */
public class MyLinksPageDefaultVisualStateTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        //cleaning session
        DebugPageObject.openDebugPage();
        DebugPageObject.cleanSession();
        //Open MyLinks Page
        MyLinksViewPageObject.openMyLinksPage();
    }

    /**
     * Tests that correct page is opened.
     */
    @Test
    public void myLinkPageShouldOpen() {
        MyLinksViewPageObject.PAGE.should(exist);
    }

    /**
     * Tests that Session Banner exist and visible.
     */
    @Test
    public void sessionBannerShouldExist() {
        MyLinksViewPageObject.Banners.SESSION_BANNER.should(exist);
        MyLinksViewPageObject.Banners.SESSION_BANNER.shouldBe(visible);
    }

    /**
     * Tests that Session Banner has needed Words.
     */
    @Test
    public void sessionBannerShouldHaveNeededWords() {
        MyLinksViewPageObject.Banners.SESSION_BANNER.shouldHave(text("session"));
        MyLinksViewPageObject.Banners.SESSION_BANNER.shouldHave(text("users"));
    }

    /**
     * Tests that No Records Banner exist and visible.
     */
    @Test
    public void noRecordsBannerShouldExist() {
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER.should(exist);
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER.shouldBe(visible);
    }

    /**
     * Tests that No Records Banner has needed Words.
     */
    @Test
    public void noRecordsBannerShouldHaveNeededWord() {
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER_TEXT.should(exist);
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER_TEXT.shouldBe(visible);
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER_TEXT.shouldHave(text("lonely"));
    }

    /**
     * Tests that Link in No Records Banner exist, visible and leads to {@link HomePage}.
     */
    @Test
    public void noRecordsBannerHasLinkToMainPage() {
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER_LINK.should(exist);
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER_LINK.shouldBe(visible);
        MyLinksViewPageObject.Banners.NO_RECORDS_BANNER_LINK.shouldHave(attribute("href",
                TestUtils.getTestUrl() + "/"));
    }

    /**
     * Tests that Grid exists and visible.
     */
    @Test
    public void gridShouldExist() {
        MyLinksViewPageObject.GRID.should(exist);
        MyLinksViewPageObject.GRID.shouldBe(visible);
    }

    /**
     * Tests that Grid has Header Row.
     */
    @Test
    public void gridShouldHaveHeader() {
        SelenideElement headerRow = MyLinksViewPageObject.Grid.Header.get().getRow();
        headerRow.should(exist);
        headerRow.shouldBe(visible);
    }

    /**
     * Tests that Grid Header Row has 4 columns.
     */
    @Test
    public void gridShouldHave4Columns() {
        ElementsCollection headerCells = MyLinksViewPageObject.Grid.Header.get().getCells();
        headerCells.shouldHave(size(FOUR));
    }

    /**
     * Tests that Link Column exist, visible and has needed Text.
     */
    @Test
    public void linkColumnHeaderShouldExistAndHaveText() {
        SelenideElement linkCell = MyLinksViewPageObject.Grid.Header.get().getLinkCell();
        linkCell.should(exist);
        linkCell.shouldBe(visible);
        linkCell.shouldHave(text("Link"));
    }

    /**
     * Tests that Description Column exist, visible and has needed Text.
     */
    @Test
    public void descriptionColumnHeaderShouldExistAndHaveText() {
        SelenideElement descriptionCell = MyLinksViewPageObject.Grid.Header.get().getDescriptionCell();
        descriptionCell.should(exist);
        descriptionCell.shouldBe(visible);
        descriptionCell.shouldHave(text("Description"));
    }

    /**
     * Tests that QR Code Column exist, visible and has needed Text.
     */
    @Test
    public void qrCodeColumnHeaderShouldExistAndHaveText() {
        SelenideElement qrCodeCell = MyLinksViewPageObject.Grid.Header.get().getQrCodeCell();
        qrCodeCell.should(exist);
        qrCodeCell.shouldBe(visible);
        qrCodeCell.shouldHave(text("QR Code"));
    }

    /**
     * Tests that Actions Column exist, visible and has needed Text.
     */
    @Test
    public void actionsColumnHeaderShouldExistAndHaveText() {
        SelenideElement actionsCell = MyLinksViewPageObject.Grid.Header.get().getActionCell();
        actionsCell.should(exist);
        actionsCell.shouldBe(visible);
        actionsCell.shouldHave(text("Actions"));
    }

    /**
     * Tests that Grid hasn't any Data Rows.
     */
    @Test
    public void gridShouldHaveNoDataRows() {
        ElementsCollection gridDataRows = MyLinksViewPageObject.Grid.GridData.get().getDataRows();
        gridDataRows.shouldHave(size(0));
    }

    /**
     * Toggle Columns Button should be present and active.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void toggleColumnsButtonShouldBePresentAndActive() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.should(exist);
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.shouldBe(visible);
    }

    /**
     * Toggle Columns Button should have tertiary style.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void toggleColumnsButtonShouldHaveTertiaryStyle() {
        TestUtils.assertHasTheme(MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON, "tertiary");
    }

    /**
     * On click on Toggle Columns Button Menu appears.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void whenClickOnToggleColumnsButton_menuAppears() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.MENU_BOX.shouldBe(visible);
        //cleanup
        MyLinksViewPageObject.ToggleColumnsMenu.closeMenu();
    }

    /**
     * Toggle Columns Menu should have 4 elements.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void toggleColumnsMenuShouldHave4Elements() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.MENU_ITEMS.shouldHave(size(FOUR));
        //cleanup
        MyLinksViewPageObject.ToggleColumnsMenu.closeMenu();
    }

    /**
     * Toggle Columns Menu has all needed elements inside.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void toggleColumnsMenuHasAllNeededElementsInside() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.ACTIONS_ITEM.shouldBe(visible);
        MyLinksViewPageObject.ToggleColumnsMenu.DESCRIPTION_ITEM.shouldBe(visible);
        MyLinksViewPageObject.ToggleColumnsMenu.QR_CODE_ITEM.shouldBe(visible);
        MyLinksViewPageObject.ToggleColumnsMenu.ACTIONS_ITEM.shouldBe(visible);
        //cleanup
        MyLinksViewPageObject.ToggleColumnsMenu.closeMenu();
    }

    /**
     * Toggle Columns Menu Elements are checked.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/756")
    public void toggleColumnsMenuShouldHaveAllElementsChecked() {
        MyLinksViewPageObject.TOGGLE_COLUMNS_BUTTON.click();
        MyLinksViewPageObject.ToggleColumnsMenu.LINK_ITEM.shouldHave(attribute("menu-item-checked"));
        MyLinksViewPageObject.ToggleColumnsMenu.DESCRIPTION_ITEM.shouldHave(attribute("menu-item-checked"));
        MyLinksViewPageObject.ToggleColumnsMenu.QR_CODE_ITEM.shouldHave(attribute("menu-item-checked"));
        MyLinksViewPageObject.ToggleColumnsMenu.ACTIONS_ITEM.shouldHave(attribute("menu-item-checked"));
        //cleanup
        MyLinksViewPageObject.ToggleColumnsMenu.closeMenu();
    }

    /**
     * Grid Filter Field should be present.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void gridFilterFieldShouldBePresent() {
        MyLinksViewPageObject.GRID_FILTER_FIELD.should(exist);
        MyLinksViewPageObject.GRID_FILTER_FIELD.shouldBe(visible);
    }

    /**
     * Grid Filter Field should have Search Icon.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void gridFilterFieldShouldHaveSearchIcon() {
        MyLinksViewPageObject.GridFilter.SEARCH_ICON.shouldBe(visible);
        MyLinksViewPageObject.GridFilter.SEARCH_ICON.shouldHave(attribute("icon", "vaadin:search"));
    }

    /**
     * Grid Filter Field should have Placeholder.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void gridFilterFieldShouldHavePlaceholder() {
        SelenideElement input = TextFieldElement.byCss("#gridFilterField").getInput();
        input.shouldHave(attribute("placeholder", "Search"));
    }

    /**
     * When no Text inside Grid Filter Field - Clean Text Button should be hidden.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void whenNoTextInsideGridFilterField_cleanTextButtonShouldBeHidden() {
        MyLinksViewPageObject.GRID_FILTER_FIELD.shouldBe(empty);
        MyLinksViewPageObject.GridFilter.CLEAR_BUTTON.shouldNotBe(visible);
    }

    /**
     * When Text inside Grid Filter Field - Clean Text Button should appear.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void whenTextInsideGridFilterField_cleanTextButtonShouldAppear() {
        MyLinksViewPageObject.GRID_FILTER_FIELD.shouldBe(empty);
        MyLinksViewPageObject.GRID_FILTER_FIELD.setValue("Test");
        MyLinksViewPageObject.GridFilter.CLEAR_BUTTON.shouldBe(visible);
        //clean up
        MyLinksViewPageObject.GridFilter.CLEAR_BUTTON.click();
    }

    /**
     * Clean Text Button clears Text.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/762")
    public void cleanTextButtonClearsText() {
        MyLinksViewPageObject.GRID_FILTER_FIELD.shouldBe(empty);
        MyLinksViewPageObject.GRID_FILTER_FIELD.setValue("CleanMe");
        MyLinksViewPageObject.GridFilter.CLEAR_BUTTON.shouldBe(visible);
        MyLinksViewPageObject.GridFilter.CLEAR_BUTTON.click();
        MyLinksViewPageObject.GRID_FILTER_FIELD.shouldBe(empty);
    }
}
