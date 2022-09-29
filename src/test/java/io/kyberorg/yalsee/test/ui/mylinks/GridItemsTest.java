package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestUtils;
import io.kyberorg.yalsee.ui.pages.mylinks.MyLinksPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link MyLinksPage} visual state with one link saved.
 *
 * @since 3.2
 */
public class GridItemsTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTest() {
        //session cleanup
        open("/myLinks");
        waitForVaadin();
        CookieBannerPageObject.closeBannerIfAny();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        CookieBannerPageObject.closeBannerIfAny(); //banner re-appears for new session

        //saving one link
        open("/");
        waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://kyberorg.io");

        //doing to page
        open("/myLinks");
        waitForVaadin();
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
     * Tests that Actions Cell has 3 Buttons.
     */
    @Test
    public void actionsCellHasThreeButtons() {
        SelenideElement actionsCell = Grid.GridData.get().getRow(1).getActionsCell();
        ElementsCollection vaadinButtons = actionsCell.$("flow-component-renderer vaadin-horizontal-layout").
                $$("vaadin-button");
        vaadinButtons.shouldHave(size(3));
    }

    /**
     * Tests that Actions Cell has Edit Button.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void actionsCellHasEditButton() {
        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        editButton.should(exist);
        editButton.shouldBe(visible);
        editButton.shouldHave(text("Edit"));
    }

    /**
     * Tests that Edit Button is active.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void editButtonIsActive() {
        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        editButton.shouldBe(enabled);
    }

    /**
     * Edit Button should have primary Theme.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void editButtonIsPrimary() {
        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        TestUtils.assertHasTheme(editButton, "primary");
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
     * Save Button should be hidden by default.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void saveButtonIsHiddenByDefault() {
        SelenideElement saveButton = Grid.GridData.get().getRow(1).getSaveButton();
        saveButton.shouldNot(exist);
    }

    /**
     * Cancel Button should be hidden by default.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void cancelButtonIsHiddenByDefault() {
        SelenideElement cancelButton = Grid.GridData.get().getRow(1).getCancelButton();
        cancelButton.shouldNot(exist);
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
