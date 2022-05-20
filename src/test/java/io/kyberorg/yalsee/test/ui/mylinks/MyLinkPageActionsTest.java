package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MainViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.pages.mylinks.MyLinksPage;
import io.kyberorg.yalsee.utils.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.constants.App.THREE;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link MyLinksPage} by performing different actions with its elements.
 *
 * @since 3.2
 */
public class MyLinkPageActionsTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        //cleaning session
        open("/myLinks");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        CookieBannerPageObject.closeBannerIfAny(); //banner re-appears for new session
    }

    /**
     * Tests that Delete Button removes Record from Grid.
     */
    @Test
    public void deleteButtonRemovesRecordFromGrid() {
        saveOneLink();
        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(1));
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.click();

        DeleteDialog.DIALOG.shouldBe(visible);
        DeleteDialog.DELETE_BUTTON.click();

        //reload needed - because Vaadin just hides element in grid
        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(0));
    }

    /**
     * On Delete Button clicked - opens Delete Confirmation Dialog.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/680")
    public void onDeleteButtonClicked_opensDeleteConfirmationDialog() {
        saveOneLink();
        openMyLinksPage();
        clickFirstDeleteButton();

        DeleteDialog.DIALOG.should(exist);
        DeleteDialog.DIALOG.shouldBe(visible);

        //cleanup
        DeleteDialog.CANCEL_BUTTON.click();
    }

    /**
     * Tests that Grid Have Size 2 if 2 Links saved.
     */
    @Test
    public void gridShouldHaveSizeTwoIfTwoLinksSaved() {
        saveOneLink();
        saveOneLink();
        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(2));
    }

    /**
     * Tests that each Row has all needed Elements.
     */
    @Test
    public void eachRowHasAllElements() {
        saveOneLink();
        saveOneLink();
        openMyLinksPage();

        Grid.GridData.Row rowOne = Grid.GridData.get().getRow(1);
        Grid.GridData.Row rowTwo = Grid.GridData.get().getRow(2);

        List<Grid.GridData.Row> rows = new ArrayList<>(2);
        rows.add(rowOne);
        rows.add(rowTwo);

        for (Grid.GridData.Row row : rows) {
            row.getLinkCell().should(exist);
            row.getLinkCell().shouldBe(visible);
            row.getLinkCell().shouldNotBe(empty);

            row.getDescriptionCell().should(exist);
            row.getDescriptionCell().shouldBe(visible);
            row.getDescriptionCell().shouldBe(empty);

            row.getQRCode().should(exist);
            row.getQRCode().shouldBe(visible);
            assertTrue(row.getQRCode().isImage());

            row.getDeleteButton().should(exist);
            row.getDeleteButton().shouldBe(visible);
            row.getDeleteButton().shouldBe(enabled);
        }
    }

    /**
     * Tests that Grid Have Size 3 if 3 Links saved.
     */
    @Test
    public void gridShouldHaveSizeThreeIfThreeLinksSaved() {
        saveOneLink();
        saveOneLink();
        saveOneLink();

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(THREE));
    }

    /**
     * Tests that each Row has own Short and Long Links.
     */
    @Test
    public void eachRowHasOwnShortAndLongLinks() {
        String longUrlOne = "https://github.com/kyberorg/yalsee/issues/195";
        String longUrlTwo = "https://gist.github.com/kyberorg/e3621b30a217addf8566736dc47eb997";

        HomePageObject.saveOneLink(longUrlOne);
        String shortUrlOne = UrlUtils.removeProtocol(HomePageObject.getSavedUrl());
        HomePageObject.saveOneLink(longUrlTwo);
        String shortUrlTwo = UrlUtils.removeProtocol(HomePageObject.getSavedUrl());

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(2));
        Grid.GridData.Row rowOne = Grid.GridData.get().getRow(1);
        Grid.GridData.Row rowTwo = Grid.GridData.get().getRow(2);

        String actualLinkOne = rowOne.getLinkCell().getText();
        assertEquals(shortUrlOne, actualLinkOne);

        String actualLinkTwo = rowTwo.getLinkCell().getText();
        assertEquals(shortUrlTwo, actualLinkTwo);

        rowOne.getDescriptionCell().click();
        String actualLongUrlOne = Grid.GridItem.Details.of(rowOne.getItemDetails()).getLongLink().getText();
        assertEquals(longUrlOne, actualLongUrlOne);

        rowTwo.getDescriptionCell().click();
        String actualLongUrlTwo = Grid.GridItem.Details.of(rowTwo.getItemDetails()).getLongLink().getText();
        assertEquals(longUrlTwo, actualLongUrlTwo);
    }

    /**
     * Tests that EndSession Button ends Session and removes All Grid Records.
     */
    @Test
    public void endSessionButtonEndsSessionAndRemovesAllGridRecords() {
        saveOneLink();
        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(1));
        END_SESSION_BUTTON.click();
        //page should be refreshed automagically
        waitForVaadin();

        Grid.GridData.get().getDataRows().shouldHave(size(0));
    }

    /**
     * When Menu item clicked Grid Column Order remains the same.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/695")
    public void onMenuClickGridColumnOrderRemainsSame() {
        MainViewPageObject.Menu.MY_LINKS_ITEM.click();
        waitForVaadin();
        SelenideElement firstCell = Grid.Header.get().getCells().get(0);
        firstCell.shouldHave(text("Link"));
    }

    /**
     * When Save Button clicked - Editor saves Value and closes.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void whenSaveButtonClicked_editorSavesValueAndCloses() {
        String newDescription = "New Description";
        saveOneLink();
        openMyLinksPage();

        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement saveButton = Grid.GridData.get().getRow(1).getSaveButton();

        editButton.click();
        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        saveButton.click();

        descriptionEditor.shouldNotBe(visible);
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    /**
     * When Cancel Button clicked - Editor discards Changes and closes.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void whenCancelButtonClicked_editorDiscardsChangesAndCloses() {
        final String originalDescription = "Kyberorg's Site";
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", originalDescription);
        openMyLinksPage();

        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement cancelButton = Grid.GridData.get().getRow(1).getCancelButton();

        editButton.click();
        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue("It is my site");
        cancelButton.click();

        descriptionEditor.shouldNotBe(visible);
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(originalDescription));
    }

    /**
     * When first Editor opened and second Edit Button Clicked - first Editor should be closed and discarding Value.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void whenFirstEditorOpenedAndSecondEditButtonClicked_firstEditorShouldBeClosedAndDiscardValue() {
        final String firstDescription = "Kyberorg's Site";
        final String secondDescription = "Kv.ee";
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", firstDescription);
        HomePageObject.saveLinkWithDescription("https://kv.ee", secondDescription);
        openMyLinksPage();

        closeGridEditorIfOpened(1);
        closeGridEditorIfOpened(2);

        SelenideElement firstEditButton = Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement secondEditButton = Grid.GridData.get().getRow(2).getEditButton();
        SelenideElement firstDescriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        SelenideElement secondDescriptionEditor = Grid.GridData.get().getRow(2).getDescriptionEditor();
        SelenideElement firstDescriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();

        firstEditButton.click();
        firstDescriptionEditor.setValue("It is my site");
        secondEditButton.click();

        firstDescriptionEditor.shouldNotBe(visible);
        firstDescriptionCell.shouldHave(text(firstDescription));
        secondDescriptionEditor.shouldBe(visible);

        //cleanup
        secondDescriptionEditor.pressEnter();
    }

    /**
     * When first Editor opened and there is double click on second item  - first Editor should be closed and
     * discarding Value.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void whenFirstEditorOpenedAndSecondItemDoubleClicked_firstEditorShouldBeClosedAndDiscardValue() {
        final String firstDescription = "Kyberorg's Site";
        final String secondDescription = "Kv.ee";
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", firstDescription);
        HomePageObject.saveLinkWithDescription("https://kv.ee", secondDescription);
        openMyLinksPage();

        closeGridEditorIfOpened(1);
        closeGridEditorIfOpened(2);

        SelenideElement firstDescriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        SelenideElement secondDescriptionEditor = Grid.GridData.get().getRow(2).getDescriptionEditor();
        SelenideElement firstDescriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        SelenideElement secondDescriptionCell = Grid.GridData.get().getRow(2).getDescriptionCell();

        firstDescriptionCell.doubleClick();
        firstDescriptionEditor.setValue("It is my site");
        secondDescriptionCell.doubleClick();

        firstDescriptionEditor.shouldNotBe(visible);
        firstDescriptionCell.shouldHave(text(firstDescription));
        secondDescriptionEditor.shouldBe(visible);

        //cleanup
        secondDescriptionEditor.pressEnter();
    }

    static void saveOneLink() {
        HomePageObject.saveOneLink("https://kyberorg.io");
    }
}