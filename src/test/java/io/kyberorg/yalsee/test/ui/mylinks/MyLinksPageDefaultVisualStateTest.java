package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestUtils;
import io.kyberorg.yalsee.test.utils.vaadin.elements.TextFieldElement;
import io.kyberorg.yalsee.ui.HomePage;
import io.kyberorg.yalsee.ui.MyLinksPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.constants.App.FOUR;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Banners.*;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Test default visual state of {@link MyLinksPage}. This state, when user just opens page without doing something else.
 */
public class MyLinksPageDefaultVisualStateTest extends SelenideTest {

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

    /**
     * Tests that correct page is opened.
     */
    @Test
    public void myLinkPageShouldOpen() {
        PAGE.should(exist);
    }

    /**
     * Tests that Session Banner exist and visible.
     */
    @Test
    public void sessionBannerShouldExist() {
        SESSION_BANNER.should(exist);
        SESSION_BANNER.shouldBe(visible);
    }

    /**
     * Tests that Session Banner has needed Words.
     */
    @Test
    public void sessionBannerShouldHaveNeededWords() {
        SESSION_BANNER.shouldHave(text("session"));
        SESSION_BANNER.shouldHave(text("users"));
    }

    /**
     * Tests that No Records Banner exist and visible.
     */
    @Test
    public void noRecordsBannerShouldExist() {
        NO_RECORDS_BANNER.should(exist);
        NO_RECORDS_BANNER.shouldBe(visible);
    }

    /**
     * Tests that No Records Banner has needed Words.
     */
    @Test
    public void noRecordsBannerShouldHaveNeededWord() {
        NO_RECORDS_BANNER_TEXT.should(exist);
        NO_RECORDS_BANNER_TEXT.shouldBe(visible);
        NO_RECORDS_BANNER_TEXT.shouldHave(text("lonely"));
    }

    /**
     * Tests that Link in No Records Banner exist, visible and leads to {@link HomePage}.
     */
    @Test
    public void noRecordsBannerHasLinkToMainPage() {
        NO_RECORDS_BANNER_LINK.should(exist);
        NO_RECORDS_BANNER_LINK.shouldBe(visible);
        NO_RECORDS_BANNER_LINK.shouldHave(attribute("href",
                TestUtils.getTestUrl() + "/"));
    }

    /**
     * Tests that End Session Button exists, visible and active.
     */
    @Test
    public void endSessionButtonShouldExistAndBeActive() {
        END_SESSION_BUTTON.should(exist);
        END_SESSION_BUTTON.shouldBe(visible);
        END_SESSION_BUTTON.shouldBe(enabled);
    }

    /**
     * Tests that Grid exists and visible.
     */
    @Test
    public void gridShouldExist() {
        GRID.should(exist);
        GRID.shouldBe(visible);
    }

    /**
     * Tests that Grid has Header Row.
     */
    @Test
    public void gridShouldHaveHeader() {
        SelenideElement headerRow = Grid.Header.get().getRow();
        headerRow.should(exist);
        headerRow.shouldBe(visible);
    }

    /**
     * Tests that Grid Header Row has 4 columns.
     */
    @Test
    public void gridShouldHave4Columns() {
        ElementsCollection headerCells = Grid.Header.get().getCells();
        headerCells.shouldHave(size(FOUR));
    }

    /**
     * Tests that Link Column exist, visible and has needed Text.
     */
    @Test
    public void linkColumnHeaderShouldExistAndHaveText() {
        SelenideElement linkCell = Grid.Header.get().getLinkCell();
        linkCell.should(exist);
        linkCell.shouldBe(visible);
        linkCell.shouldHave(text("Link"));
    }

    /**
     * Tests that Description Column exist, visible and has needed Text.
     */
    @Test
    public void descriptionColumnHeaderShouldExistAndHaveText() {
        SelenideElement descriptionCell = Grid.Header.get().getDescriptionCell();
        descriptionCell.should(exist);
        descriptionCell.shouldBe(visible);
        descriptionCell.shouldHave(text("Description"));
    }

    /**
     * Tests that QR Code Column exist, visible and has needed Text.
     */
    @Test
    public void qrCodeColumnHeaderShouldExistAndHaveText() {
        SelenideElement qrCodeCell = Grid.Header.get().getQrCodeCell();
        qrCodeCell.should(exist);
        qrCodeCell.shouldBe(visible);
        qrCodeCell.shouldHave(text("QR Code"));
    }

    /**
     * Tests that Actions Column exist, visible and has needed Text.
     */
    @Test
    public void actionsColumnHeaderShouldExistAndHaveText() {
        SelenideElement actionsCell = Grid.Header.get().getActionCell();
        actionsCell.should(exist);
        actionsCell.shouldBe(visible);
        actionsCell.shouldHave(text("Actions"));
    }

    /**
     * Tests that Grid hasn't any Data Rows.
     */
    @Test
    public void gridShouldHaveNoDataRows() {
        ElementsCollection gridDataRows = Grid.GridData.get().getDataRows();
        gridDataRows.shouldHave(size(0));
    }

    /**
     * Toggle Columns Button should be present and active.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void toggleColumnsButtonShouldBePresentAndActive() {
        TOGGLE_COLUMNS_BUTTON.should(exist);
        TOGGLE_COLUMNS_BUTTON.shouldBe(visible);
    }

    /**
     * Toggle Columns Button should have tertiary style.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void toggleColumnsButtonShouldHaveTertiaryStyle() {
        TestUtils.assertHasTheme(TOGGLE_COLUMNS_BUTTON, "tertiary");
    }

    /**
     * On click on Toggle Columns Button Menu appears.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void whenClickOnToggleColumnsButton_menuAppears() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.MENU_BOX.shouldBe(visible);
        //cleanup
        ToggleColumnsMenu.closeMenu();
    }

    /**
     * Toggle Columns Menu should have 4 elements.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void toggleColumnsMenuShouldHave4Elements() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.MENU_ITEMS.shouldHave(size(FOUR));
        //cleanup
        ToggleColumnsMenu.closeMenu();
    }

    /**
     * Toggle Columns Menu has all needed elements inside.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void toggleColumnsMenuHasAllNeededElementsInside() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.ACTIONS_ITEM.shouldBe(visible);
        ToggleColumnsMenu.DESCRIPTION_ITEM.shouldBe(visible);
        ToggleColumnsMenu.QR_CODE_ITEM.shouldBe(visible);
        ToggleColumnsMenu.ACTIONS_ITEM.shouldBe(visible);
        //cleanup
        ToggleColumnsMenu.closeMenu();
    }

    /**
     * Toggle Columns Menu Elements are checked.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/756")
    public void toggleColumnsMenuShouldHaveAllElementsChecked() {
        TOGGLE_COLUMNS_BUTTON.click();
        ToggleColumnsMenu.LINK_ITEM.shouldHave(attribute("menu-item-checked"));
        ToggleColumnsMenu.DESCRIPTION_ITEM.shouldHave(attribute("menu-item-checked"));
        ToggleColumnsMenu.QR_CODE_ITEM.shouldHave(attribute("menu-item-checked"));
        ToggleColumnsMenu.ACTIONS_ITEM.shouldHave(attribute("menu-item-checked"));
        //cleanup
        ToggleColumnsMenu.closeMenu();
    }

    /**
     * Grid Filter Field should be present.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void gridFilterFieldShouldBePresent() {
        GRID_FILTER_FIELD.should(exist);
        GRID_FILTER_FIELD.shouldBe(visible);
    }

    /**
     * Grid Filter Field should have Search Icon.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void gridFilterFieldShouldHaveSearchIcon() {
        GridFilter.SEARCH_ICON.shouldBe(visible);
        GridFilter.SEARCH_ICON.shouldHave(attribute("icon", "vaadin:search"));
    }

    /**
     * Grid Filter Field should have Placeholder.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void gridFilterFieldShouldHavePlaceholder() {
        SelenideElement input = TextFieldElement.byCss("#gridFilterField").getInput();
        input.shouldHave(attribute("placeholder", "Search"));
    }

    /**
     * When no Text inside Grid Filter Field - Clean Text Button should be hidden.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void whenNoTextInsideGridFilterField_cleanTextButtonShouldBeHidden() {
        GRID_FILTER_FIELD.shouldBe(empty);
        GridFilter.CLEAR_BUTTON.shouldNotBe(visible);
    }

    /**
     * When Text inside Grid Filter Field - Clean Text Button should appear.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void whenTextInsideGridFilterField_cleanTextButtonShouldAppear() {
        GRID_FILTER_FIELD.shouldBe(empty);
        GRID_FILTER_FIELD.setValue("Test");
        GridFilter.CLEAR_BUTTON.shouldBe(visible);
        //clean up
        GridFilter.CLEAR_BUTTON.click();
    }

    /**
     * Clean Text Button clears Text.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/762")
    public void cleanTextButtonClearsText() {
        GRID_FILTER_FIELD.shouldBe(empty);
        GRID_FILTER_FIELD.setValue("CleanMe");
        GridFilter.CLEAR_BUTTON.shouldBe(visible);
        GridFilter.CLEAR_BUTTON.click();
        GRID_FILTER_FIELD.shouldBe(empty);
    }
}
