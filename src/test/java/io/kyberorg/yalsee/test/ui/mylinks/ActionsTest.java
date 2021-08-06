package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.NotFoundViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.utils.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionsTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
        tuneDriverWithCapabilities();

        //cleaning session
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
        cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.
    }

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

    @Test
    public void gridShouldHaveSizeTwoIfTwoLinksSaved() {
        saveOneLink();
        saveOneLink();
        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(2));
    }

    @Test
    public void eachItemHasAllElements() {
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

    @Test
    public void gridShouldHaveSizeThreeIfThreeLinksSaved() {
        saveOneLink();
        saveOneLink();
        saveOneLink();

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(3));
    }

    @Test
    public void saveThreeLinksDeleteOneTwoShouldStay() {
        saveOneLink();
        saveOneLink();
        saveOneLink();

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(3));

        SelenideElement firstDeleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        SelenideElement secondDeleteButton = Grid.GridData.get().getRow(2).getDeleteButton();

        secondDeleteButton.click();
        firstDeleteButton.click();

        //wait for action completes
        VaadinPageObject.waitForVaadin();

        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    @Test
    public void eachItemHasCorrectShortAndLongLinks() {
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

    private void saveOneLink() {
        saveOneLink("https://kyberorg.io");
    }

    private void saveOneLink(final String url) {
        open("/");
        VaadinPageObject.waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt(url);
    }

    public void openMyLinksPage() {
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
    }
}
