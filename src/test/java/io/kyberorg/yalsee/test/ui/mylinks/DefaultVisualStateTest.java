package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.HomeView;
import io.kyberorg.yalsee.ui.MyLinksView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Banners.*;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Test default visual state of {@link MyLinksView}. This state, when user just opens page without doing something else.
 */
@Execution(ExecutionMode.CONCURRENT)
public class DefaultVisualStateTest extends SelenideTest {
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
        waitForVaadin();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        pageOpened = true;
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
     * Tests that Link in No Records Banner exist, visible and leads to {@link HomeView}.
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
        headerCells.shouldHave(size(4));
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
}
