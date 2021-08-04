package io.kyberorg.yalsee.test.ui.mylinks;

import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Banners.*;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid.GRID;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.PAGE;

public class MyLinksVisualStateTest extends SelenideTest {

    //emulating @BeforeAll behavior
    // this needed because tuneDriverWithCapabilities(); is not static
    private boolean pageOpened = false;

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
        SESSION_BANNER.shouldHave(text("sessions"));
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
        NO_RECORDS_BANNER_LINK.shouldHave(attribute("href", "/"));
    }

    @Test
    public void gridShouldExist() {
        GRID.should(exist);
        GRID.shouldBe(visible);
    }

    @Test
    public void gridShouldHaveHeader() {
        Grid.Header.ROW.should(exist);
        Grid.Header.ROW.shouldBe(visible);
    }

    @Test
    public void gridShouldHave4Columns() {
        Grid.Header.CELLS.shouldHave(size(4));
    }

    @Test
    public void linkColumnHeaderShouldExistAndHaveText() {
        Grid.Header.LINK_CELL.should(exist);
        Grid.Header.LINK_CELL.shouldBe(visible);
        Grid.Header.LINK_CELL.shouldHave(text("Link"));
    }

    @Test
    public void descriptionColumnHeaderShouldExistAndHaveText() {
        Grid.Header.DESCRIPTION_CELL.should(exist);
        Grid.Header.DESCRIPTION_CELL.shouldBe(visible);
        Grid.Header.DESCRIPTION_CELL.shouldHave(text("Description"));
    }

    @Test
    public void qrCodeColumnHeaderShouldExistAndHaveText() {
        Grid.Header.QR_CODE_CELL.should(exist);
        Grid.Header.QR_CODE_CELL.shouldBe(visible);
        Grid.Header.QR_CODE_CELL.shouldHave(text("QR Code"));
    }

    @Test
    public void actionsColumnHeaderShouldExistAndHaveText() {
        Grid.Header.ACTIONS_CELL.should(exist);
        Grid.Header.ACTIONS_CELL.shouldBe(visible);
        Grid.Header.ACTIONS_CELL.shouldHave(text("Actions"));
    }

    @Test
    public void gridShouldHaveNoItems() {
        Grid.Data.ROWS.shouldHave(size(0));
    }
}
