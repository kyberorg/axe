package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.NotFoundViewPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.MyLinksView;
import io.kyberorg.yalsee.ui.err.PageNotFoundView;
import io.kyberorg.yalsee.utils.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link MyLinksView} by performing different actions with its elements.
 *
 * @since 3.2
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActionsTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();

        //cleaning session
        open("/myLinks");
        waitForVaadin();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
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

        //reload needed - because Vaadin just hides element in grid
        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(0));
    }

    /**
     * Tests that Delete Button really deletes Record,
     * so link is no longer in system and {@link PageNotFoundView} appears.
     */
    @Test
    public void deleteButtonDeletesRecord() {
        saveOneLink();
        String shortUrl = HomePageObject.getSavedUrl();

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(1));
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.click();

        open(shortUrl);

        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
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

        Grid.GridData.get().getDataRows().shouldHave(size(3));
    }

    /**
     * Tests that each Row has own Short and Long Links.
     */
    @Test
    public void eachRowHasOwnShortAndLongLinks() {
        String longUrlOne = "https://github.com/kyberorg/yalsee/issues/195";
        String longUrlTwo = "https://gist.github.com/kyberorg/e3621b30a217addf8566736dc47eb997";

        saveOneLink(longUrlOne);
        String shortUrlOne = UrlUtils.removeProtocol(HomePageObject.getSavedUrl());
        saveOneLink(longUrlTwo);
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

    private void saveOneLink() {
        saveOneLink("https://kyberorg.io");
    }

    private void saveOneLink(final String url) {
        open("/");
        waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt(url);
    }

    private void openMyLinksPage() {
        open("/myLinks");
        waitForVaadin();
    }
}
