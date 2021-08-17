package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.MyLinksView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link MyLinksView} visual state with one link saved.
 *
 * @since 3.2
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GridItemsTest extends SelenideTest {
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

        //session cleanup
        open("/myLinks");
        waitForVaadin();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.

        //saving one link
        open("/");
        waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://kyberorg.io");

        //doing to page
        open("/myLinks");
        waitForVaadin();

        pageOpened = true;
    }

    /**
     * Tests that Session Banner exists.
     */
    @Test
    public void sessionBannerShouldExist() {
        Banners.SESSION_BANNER.should(exist);
        Banners.SESSION_BANNER.shouldBe(visible);
    }

    /**
     * Tests that No Records Banner is hidden.
     */
    @Test
    public void noRecordsBannerShouldBeHidden() {
        Banners.NO_RECORDS_BANNER.shouldBe(hidden);
        Banners.NO_RECORDS_BANNER_TEXT.shouldBe(hidden);
        Banners.NO_RECORDS_BANNER_LINK.shouldBe(hidden);
    }

    /**
     * Tests that Grid has only one Item.
     */
    @Test
    public void gridHasOneItem() {
        Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    /**
     * Tests that Link Cell contains String with Short Link.
     */
    @Test
    public void linkCellContainsStringWithShortLink() {
        SelenideElement link = Grid.GridData.get().getRow(1).getLinkCell();
        link.should(exist);
        link.shouldBe(visible);
        link.shouldHave(text("/"));
        link.shouldHave(text(TestUtils.getAppShortDomain()));
    }

    /**
     * Tests that Description Cell is empty.
     */
    @Test
    public void descriptionCellShouldBeEmpty() {
        SelenideElement description = Grid.GridData.get().getRow(1).getDescriptionCell();
        description.should(exist);
        description.shouldBe(visible);
        description.shouldBe(empty);
    }

    /**
     * Tests that QR Code Cell has QR Code.
     */
    @Test
    public void qrCodeCellHasQRCode() {
        SelenideElement qrCodeCell = Grid.GridData.get().getRow(1).getQRCodeCell();
        qrCodeCell.should(exist);
        qrCodeCell.shouldBe(visible);

        SelenideElement qrCode = Grid.GridData.get().getRow(1).getQRCode();
        qrCode.should(exist);
        assertTrue(qrCode.isImage(), "QR code is not image");

        qrCode.shouldHave(attribute("src"));
        assertTrue(TestUtils.isQRCode(qrCode.getAttribute("src")), "Image is not valid QR Code");

        qrCode.shouldHave(attribute("alt", "QR Code"));
    }

    /**
     * Tests that QR Code Cell is clickable.
     */
    @Test
    public void qrCodeCellIsClickable() {
        SelenideElement qrCodeCell = Grid.GridData.get().getRow(1).getQRCode();
        qrCodeCell.shouldBe(enabled);
    }

    /**
     * Tests that Actions Cell has one Button.
     */
    @Test
    public void actionsCellHasOneButton() {
        SelenideElement actionsCell = Grid.GridData.get().getRow(1).getActionsCell();
        ElementsCollection vaadinButtons = actionsCell.$("flow-component-renderer").$$("vaadin-button");
        vaadinButtons.shouldHave(size(1));
    }

    /**
     * Tests that Actions Cell has Delete Button.
     */
    @Test
    public void actionsCellHasDeleteButton() {
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.should(exist);
        deleteButton.shouldBe(visible);
        deleteButton.shouldHave(text("Delete"));
    }

    /**
     * Tests that Delete Button is active.
     */
    @Test
    public void deleteButtonIsActive() {
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.shouldBe(enabled);
    }

    /**
     * Tests that ItemDetails Element is hidden.
     */
    @Test
    public void itemDetailsShouldBeHidden() {
        SelenideElement itemDetails = Grid.GridData.get().getRow(1).getItemDetails();
        itemDetails.shouldNot(exist);
    }
}
