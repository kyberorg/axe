package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MainViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.MyLinksView;
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
 * Tests {@link MyLinksView} by performing different actions with its elements.
 *
 * @since 3.2
 */
public class ActionsTest extends SelenideTest {

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

        MyLinksViewPageObject.saveOneLink(longUrlOne);
        String shortUrlOne = UrlUtils.removeProtocol(HomePageObject.getSavedUrl());
        MyLinksViewPageObject.saveOneLink(longUrlTwo);
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

    static void saveOneLink() {
        MyLinksViewPageObject.saveOneLink("https://kyberorg.io");
    }
}
