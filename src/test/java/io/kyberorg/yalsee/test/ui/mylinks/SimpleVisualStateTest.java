package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Banners.*;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;

public class SimpleVisualStateTest extends SelenideTest {
    //emulating @BeforeAll behavior
    // this needed because tuneDriverWithCapabilities(); is not static
    private static boolean pageOpened = false;

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
        if (pageOpened) {
            return;
        }
        tuneDriverWithCapabilities();
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
        cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.
        pageOpened = true;
    }

    @Test
    public void myLinkPageShouldOpen() {
        PAGE.should(exist);
    }

    @Test
    public void sessionBannerShouldExist() {
        SESSION_BANNER.should(exist);
        SESSION_BANNER.shouldBe(visible);
    }

    @Test
    public void sessionBannerShouldHaveNeededWords() {
        SESSION_BANNER.shouldHave(text("session"));
        SESSION_BANNER.shouldHave(text("users"));
    }

    @Test
    public void noRecordsBannerShouldExist() {
        NO_RECORDS_BANNER.should(exist);
        NO_RECORDS_BANNER.shouldBe(visible);
    }

    @Test
    public void noRecordsBannerShouldHaveWordLonely() {
        NO_RECORDS_BANNER_TEXT.should(exist);
        NO_RECORDS_BANNER_TEXT.shouldBe(visible);
        NO_RECORDS_BANNER_TEXT.shouldHave(text("lonely"));
    }

    @Test
    public void noRecordsBannerHasLinkToMainPage() {
        NO_RECORDS_BANNER_LINK.should(exist);
        NO_RECORDS_BANNER_LINK.shouldBe(visible);
        NO_RECORDS_BANNER_LINK.shouldHave(attribute("href", TestUtils.getTestUrl() + "/"));
    }

    @Test
    public void gridShouldExist() {
        GRID.should(exist);
        GRID.shouldBe(visible);
    }

    @Test
    public void gridShouldHaveHeader() {
        SelenideElement headerRow = Grid.Header.get().getRow();
        headerRow.should(exist);
        headerRow.shouldBe(visible);
    }

    @Test
    public void gridShouldHave4Columns() {
        ElementsCollection headerCells = Grid.Header.get().getCells();
        headerCells.shouldHave(size(4));
    }

    @Test
    public void linkColumnHeaderShouldExistAndHaveText() {
        SelenideElement linkCell = Grid.Header.get().getLinkCell();
        linkCell.should(exist);
        linkCell.shouldBe(visible);
        linkCell.shouldHave(text("Link"));
    }

    @Test
    public void descriptionColumnHeaderShouldExistAndHaveText() {
        SelenideElement descriptionCell = Grid.Header.get().getDescriptionCell();
        descriptionCell.should(exist);
        descriptionCell.shouldBe(visible);
        descriptionCell.shouldHave(text("Description"));
    }

    @Test
    public void qrCodeColumnHeaderShouldExistAndHaveText() {
        SelenideElement qrCodeCell = Grid.Header.get().getQrCodeCell();
        qrCodeCell.should(exist);
        qrCodeCell.shouldBe(visible);
        qrCodeCell.shouldHave(text("QR Code"));
    }

    @Test
    public void actionsColumnHeaderShouldExistAndHaveText() {
        SelenideElement actionsCell = Grid.Header.get().getActionCell();
        actionsCell.should(exist);
        actionsCell.shouldBe(visible);
        actionsCell.shouldHave(text("Actions"));
    }

    @Test
    public void gridShouldHaveNoItems() {
        ElementsCollection gridDataRows = Grid.GridData.get().getDataRows();
        gridDataRows.shouldHave(size(0));
    }
}
