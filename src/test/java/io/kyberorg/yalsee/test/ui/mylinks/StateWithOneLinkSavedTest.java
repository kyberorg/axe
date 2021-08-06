package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StateWithOneLinkSavedTest extends SelenideTest {
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
        VaadinPageObject.waitForVaadin();
        cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.

        //saving one link
        open("/");
        VaadinPageObject.waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://kyberorg.io");

        //doing to page
        open("/myLinks");
        VaadinPageObject.waitForVaadin();

        pageOpened = true;
    }

    @Test
    public void sessionBannerShouldExist() {
        Banners.SESSION_BANNER.should(exist);
        Banners.SESSION_BANNER.shouldBe(visible);
    }

    @Test
    public void noRecordsBannerShouldBeHidden() {
        Banners.NO_RECORDS_BANNER.shouldBe(hidden);
        Banners.NO_RECORDS_BANNER_TEXT.shouldBe(hidden);
        Banners.NO_RECORDS_BANNER_LINK.shouldBe(hidden);
    }

    @Test
    public void gridHasOneItem() {
        Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    @Test
    public void linkCellContainsStringWithShortLink() {
        SelenideElement link = Grid.GridData.get().getRow(1).getLinkCell();
        link.should(exist);
        link.shouldBe(visible);
        link.shouldHave(text("/"));
        link.shouldHave(text(TestUtils.getAppShortDomain()));
    }

    @Test
    public void descriptionCellShouldBeEmpty() {
        SelenideElement description = Grid.GridData.get().getRow(1).getDescriptionCell();
        description.should(exist);
        description.shouldBe(visible);
        description.shouldBe(empty);
    }

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

    @Test
    public void qrCodeCellIsClickable() {
        SelenideElement qrCodeCell = Grid.GridData.get().getRow(1).getQRCode();
        qrCodeCell.shouldBe(enabled);
    }

    @Test
    public void actionsCellHasOneButton() {
        SelenideElement actionsCell = Grid.GridData.get().getRow(1).getActionsCell();
        ElementsCollection vaadinButtons = actionsCell.$("flow-component-renderer").$$("vaadin-button");
        vaadinButtons.shouldHave(size(1));
    }

    @Test
    public void actionsCellHasDeleteButton() {
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.should(exist);
        deleteButton.shouldBe(visible);
        deleteButton.shouldHave(text("Delete"));
    }

    @Test
    public void deleteButtonIsActive() {
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.shouldBe(enabled);
    }

    @Test
    public void itemDetailsShouldBeHidden() {
        SelenideElement itemDetails = Grid.GridData.get().getRow(1).getItemDetails();
        itemDetails.shouldNot(exist);
    }
}
