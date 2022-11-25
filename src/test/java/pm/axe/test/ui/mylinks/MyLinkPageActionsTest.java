package pm.axe.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.MainViewPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.mylinks.MyLinksPage;
import pm.axe.utils.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static pm.axe.constants.App.THREE;
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
        VaadinPageObject.waitForVaadin();
        MyLinksViewPageObject.cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.
    }

    /**
     * Tests that Delete Button removes Record from Grid.
     */
    @Test
    public void deleteButtonRemovesRecordFromGrid() {
        saveOneLink();
        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));
        SelenideElement deleteButton = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.click();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldBe(visible);
        MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON.click();

        //reload needed - because Vaadin just hides element in grid
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(0));
    }

    /**
     * On Delete Button clicked - opens Delete Confirmation Dialog.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/680")
    public void onDeleteButtonClicked_opensDeleteConfirmationDialog() {
        saveOneLink();
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.clickFirstDeleteButton();

        MyLinksViewPageObject.DeleteDialog.DIALOG.should(exist);
        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldBe(visible);

        //cleanup
        MyLinksViewPageObject.DeleteDialog.CANCEL_BUTTON.click();
    }

    /**
     * Tests that Grid Have Size 2 if 2 Links saved.
     */
    @Test
    public void gridShouldHaveSizeTwoIfTwoLinksSaved() {
        saveOneLink();
        saveOneLink();
        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(2));
    }

    /**
     * Tests that each Row has all needed Elements.
     */
    @Test
    public void eachRowHasAllElements() {
        saveOneLink();
        saveOneLink();
        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.Row rowOne = MyLinksViewPageObject.Grid.GridData.get().getRow(1);
        MyLinksViewPageObject.Grid.GridData.Row rowTwo = MyLinksViewPageObject.Grid.GridData.get().getRow(2);

        List<MyLinksViewPageObject.Grid.GridData.Row> rows = new ArrayList<>(2);
        rows.add(rowOne);
        rows.add(rowTwo);

        for (MyLinksViewPageObject.Grid.GridData.Row row : rows) {
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

        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(THREE));
    }

    /**
     * Tests that each Row has own Short and Long Links.
     */
    @Test
    public void eachRowHasOwnShortAndLongLinks() {
        String longUrlOne = "https://github.com/kyberorg/axe/issues/195";
        String longUrlTwo = "https://gist.github.com/kyberorg/e3621b30a217addf8566736dc47eb997";

        HomePageObject.saveOneLink(longUrlOne);
        String shortUrlOne = UrlUtils.removeProtocol(HomePageObject.getSavedUrl());
        HomePageObject.saveOneLink(longUrlTwo);
        String shortUrlTwo = UrlUtils.removeProtocol(HomePageObject.getSavedUrl());

        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(2));
        MyLinksViewPageObject.Grid.GridData.Row rowOne = MyLinksViewPageObject.Grid.GridData.get().getRow(1);
        MyLinksViewPageObject.Grid.GridData.Row rowTwo = MyLinksViewPageObject.Grid.GridData.get().getRow(2);

        String actualLinkOne = rowOne.getLinkCell().getText();
        assertEquals(shortUrlOne, actualLinkOne);

        String actualLinkTwo = rowTwo.getLinkCell().getText();
        assertEquals(shortUrlTwo, actualLinkTwo);

        rowOne.getDescriptionCell().click();
        String actualLongUrlOne = MyLinksViewPageObject.Grid.GridItem.Details.of(rowOne.getItemDetails()).getLongLink().getText();
        assertEquals(longUrlOne, actualLongUrlOne);

        rowTwo.getDescriptionCell().click();
        String actualLongUrlTwo = MyLinksViewPageObject.Grid.GridItem.Details.of(rowTwo.getItemDetails()).getLongLink().getText();
        assertEquals(longUrlTwo, actualLongUrlTwo);
    }

    /**
     * Tests that EndSession Button ends Session and removes All Grid Records.
     */
    @Test
    public void endSessionButtonEndsSessionAndRemovesAllGridRecords() {
        saveOneLink();
        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));
        MyLinksViewPageObject.END_SESSION_BUTTON.click();
        //page should be refreshed automagically
        VaadinPageObject.waitForVaadin();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(0));
    }

    /**
     * When Menu item clicked Grid Column Order remains the same.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/695")
    public void onMenuClickGridColumnOrderRemainsSame() {
        MainViewPageObject.Menu.MY_LINKS_ITEM.click();
        VaadinPageObject.waitForVaadin();
        SelenideElement firstCell = MyLinksViewPageObject.Grid.Header.get().getCells().get(0);
        firstCell.shouldHave(text("Link"));
    }

    /**
     * When Save Button clicked - Editor saves Value and closes.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/679")
    public void whenSaveButtonClicked_editorSavesValueAndCloses() {
        String newDescription = "New Description";
        saveOneLink();
        MyLinksViewPageObject.openMyLinksPage();

        SelenideElement editButton = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement saveButton = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getSaveButton();

        editButton.click();
        SelenideElement descriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        saveButton.click();

        descriptionEditor.shouldNotBe(visible);
        SelenideElement descriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    /**
     * When Cancel Button clicked - Editor discards Changes and closes.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/679")
    public void whenCancelButtonClicked_editorDiscardsChangesAndCloses() {
        final String originalDescription = "Kyberorg's Site";
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", originalDescription);
        MyLinksViewPageObject.openMyLinksPage();

        SelenideElement editButton = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement cancelButton = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getCancelButton();

        editButton.click();
        SelenideElement descriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue("It is my site");
        cancelButton.click();

        descriptionEditor.shouldNotBe(visible);
        SelenideElement descriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(originalDescription));
    }

    /**
     * When first Editor opened and second Edit Button Clicked - first Editor should be closed and discarding Value.
     */
    @Test
    @Issue("https://github.com/kyberorg/axe/issues/679")
    public void whenFirstEditorOpenedAndSecondEditButtonClicked_firstEditorShouldBeClosedAndDiscardValue() {
        final String firstDescription = "Kyberorg's Site";
        final String secondDescription = "Kv.ee";
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", firstDescription);
        HomePageObject.saveLinkWithDescription("https://kv.ee", secondDescription);
        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.closeGridEditorIfOpened(1);
        MyLinksViewPageObject.closeGridEditorIfOpened(2);

        SelenideElement firstEditButton = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement secondEditButton = MyLinksViewPageObject.Grid.GridData.get().getRow(2).getEditButton();
        SelenideElement firstDescriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        SelenideElement secondDescriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(2).getDescriptionEditor();
        SelenideElement firstDescriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();

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
    @Issue("https://github.com/kyberorg/axe/issues/679")
    public void whenFirstEditorOpenedAndSecondItemDoubleClicked_firstEditorShouldBeClosedAndDiscardValue() {
        final String firstDescription = "Kyberorg's Site";
        final String secondDescription = "Kv.ee";
        HomePageObject.saveLinkWithDescription("https://kyberorg.io", firstDescription);
        HomePageObject.saveLinkWithDescription("https://kv.ee", secondDescription);
        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.closeGridEditorIfOpened(1);
        MyLinksViewPageObject.closeGridEditorIfOpened(2);

        SelenideElement firstDescriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        SelenideElement secondDescriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(2).getDescriptionEditor();
        SelenideElement firstDescriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        SelenideElement secondDescriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(2).getDescriptionCell();

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
