package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junitpioneer.jupiter.Issue;
import org.selenide.selenoid.SelenoidClipboard;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests Grid state by interacting with its cells.
 *
 * @since 3.2
 */
public class GridCellsTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        //cleaning session
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
     * Tests that on Item Click ItemDetails are opened and have all Needed Elements inside.
     */
    @Test
    public void onItemClickItemDetailsOpenedAndHaveAllNeededElementsInside() {
        SelenideElement item = Grid.GridData.get().getRow(1).getLinkCell();
        item.click();
        SelenideElement itemDetailsElement = Grid.GridData.get().getRow(1).getItemDetails();
        Grid.GridItem.Details itemDetails = Grid.GridItem.Details.of(itemDetailsElement);
        itemDetailsElement.should(exist);
        itemDetailsElement.shouldBe(visible);

        //long link
        SelenideElement longLink = itemDetails.getLongLink();
        longLink.should(exist);
        longLink.shouldBe(visible);
        assertEquals("a", longLink.getTagName());
        String longLinkText = longLink.getText();
        longLink.shouldHave(attribute("href", longLinkText + "/"));

        //created time label
        SelenideElement createdTimeLabel = itemDetails.getCreatedTimeLabel();
        createdTimeLabel.should(exist);
        createdTimeLabel.shouldBe(visible);
        createdTimeLabel.shouldNotBe(empty);
        createdTimeLabel.shouldHave(text("Created"));

        //created time
        SelenideElement createdTime = itemDetails.getCreatedTime();
        createdTime.should(exist);
        createdTime.shouldBe(visible);
        createdTime.shouldNotBe(empty);

        //updated time label
        SelenideElement updatedTimeLabel = itemDetails.getUpdatedTimeLabel();
        updatedTimeLabel.should(exist);
        updatedTimeLabel.shouldBe(visible);
        updatedTimeLabel.shouldNotBe(empty);
        updatedTimeLabel.shouldHave(text("Updated"));

        //updated time
        SelenideElement updatedTime = itemDetails.getUpdatedTime();
        updatedTime.should(exist);
        updatedTime.shouldBe(visible);
        updatedTime.shouldNotBe(empty);

        //created time = updated time
        assertEquals(createdTime.getText(), updatedTime.getText());

        //clean up
        if (itemDetailsElement.isDisplayed()) {
            item.click();
        }
    }

    /**
     * Tests that on Click to Link Short Link copied to Clipboard.
     */
    @Test
    public void onClickToLinkShortLinkCopiedToClipboard() {
        if (isRemoteRun()) {
            assertThat(clipboard()).isInstanceOf(SelenoidClipboard.class);
        }
        SelenideTest.cleanClipboard();

        SelenideElement linkSpan = Grid.GridData.get().getRow(1).getLinkSpan();
        String linkText = linkSpan.getText();
        assertTrue(StringUtils.isNotBlank(linkText));
        String ident = linkText.replace(TestUtils.getAppShortDomain(), ""); //will leave only "/ident" part

        linkSpan.click();
        sleep(App.ONE_SECOND_IN_MILLIS);
        String textFromClipboard = clipboard().getText();
        assertEquals(TestUtils.getAppShortUrl() + ident, textFromClipboard);

        //closing item details if opened, which opens within same click
        SelenideElement itemDetails = Grid.GridData.get().getRow(1).getItemDetails();
        if (itemDetails.isDisplayed()) {
            linkSpan.click();
        }
    }

    /**
     * Tests that on double Click to Description opens Editor.
     */
    @Test
    public void onDoubleClickToDescriptionEditorOpens() {
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();
        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.should(exist);
        descriptionEditor.should(visible);

        //cleanup
        descriptionEditor.pressEscape();
    }

    /**
     * Tests that on Click to QR Code opens Modal with big QR Code.
     */
    @Test
    public void onClickToQRCodeModalOpens() {
        SelenideElement qrCode = Grid.GridData.get().getRow(1).getQRCode();
        qrCode.click();
        Grid.GridItem.BigQRCodeModal.MODAL.should(exist);
        Grid.GridItem.BigQRCodeModal.MODAL.should(visible);

        //cleanup
        Grid.GridItem.BigQRCodeModal.MODAL.pressEscape();
    }

    /**
     * Tests that big QR Code Modal has QR Code inside.
     */
    @Test
    public void bigQRCodeModalHasQRCode() {
        SelenideElement qrCode = Grid.GridData.get().getRow(1).getQRCode();
        qrCode.click();
        SelenideElement bigQRCode = Grid.GridItem.BigQRCodeModal.QR_CODE;
        bigQRCode.should(exist);
        bigQRCode.should(visible);

        assertTrue(bigQRCode.isImage(), "QR code is not image");
        bigQRCode.shouldHave(attribute("src"));
        assertTrue(TestUtils.isQRCode(bigQRCode.getAttribute("src")), "Image is not valid QR Code");
        bigQRCode.shouldHave(attribute("alt", "QR Code"));

        //cleanup
        Grid.GridItem.BigQRCodeModal.MODAL.pressEscape();
    }

    /**
     * Tests that big QR Code has Correct Size.
     */
    @Test
    public void bigQRCodeHasCorrectSize() {
        final int exceptedWidth = 350;
        final int exceptedHeight = 350;

        SelenideElement qrCode = Grid.GridData.get().getRow(1).getQRCode();
        qrCode.click();
        SelenideElement bigQRCode = Grid.GridItem.BigQRCodeModal.QR_CODE;

        assertEquals(exceptedWidth, bigQRCode.getSize().getWidth());
        assertEquals(exceptedHeight, bigQRCode.getSize().getHeight());

        //cleanup
        Grid.GridItem.BigQRCodeModal.MODAL.pressEscape();
    }

    /**
     * Edit Button should open Editor.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void editButtonShouldOpenEditor() {
        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        editButton.click();

        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.should(exist);
        descriptionEditor.shouldBe(visible);

        //clean-up
        descriptionEditor.pressEnter();
    }

    /**
     * Action Buttons are changed, when editor opens by click on edit button.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void actionButtonsAreChanged_whenEditorOpensByClickOnEditButton() {
        closeGridEditorIfOpened(1);

        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        SelenideElement saveButton = Grid.GridData.get().getRow(1).getSaveButton();
        SelenideElement cancelButton = Grid.GridData.get().getRow(1).getCancelButton();

        editButton.click();

        editButton.shouldNotBe(visible);
        deleteButton.shouldNotBe(visible);
        saveButton.shouldBe(visible);
        cancelButton.shouldBe(visible);

        //clean-up
        cancelButton.click();
    }

    /**
     * Action Buttons are changed, when editor opens by double-click on description.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void actionButtonsAreChanged_whenEditorOpensByDoubleClickOnDescription() {
        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        SelenideElement saveButton = Grid.GridData.get().getRow(1).getSaveButton();
        SelenideElement cancelButton = Grid.GridData.get().getRow(1).getCancelButton();

        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        editButton.shouldNotBe(visible);
        deleteButton.shouldNotBe(visible);
        saveButton.shouldBe(visible);
        cancelButton.shouldBe(visible);

        //cleanup
        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.pressEnter();
    }

    /**
     * Action Buttons are changed back, when editor closes.
     */
    @Test
    @Issue("https://github.com/kyberorg/yalsee/issues/679")
    public void actionButtonsAreChangedBack_whenEditorCloses() {
        closeGridEditorIfOpened(1);

        SelenideElement editButton = Grid.GridData.get().getRow(1).getEditButton();
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        SelenideElement saveButton = Grid.GridData.get().getRow(1).getSaveButton();
        SelenideElement cancelButton = Grid.GridData.get().getRow(1).getCancelButton();

        //open editor
        editButton.click();
        //close editor
        cancelButton.click();

        editButton.shouldBe(visible);
        deleteButton.shouldBe(visible);
        saveButton.shouldNotBe(visible);
        cancelButton.shouldNotBe(visible);
    }

    /**
     * Tests that Delete Button exists and active.
     */
    @Test
    public void deleteButtonShouldExistAndBeActive() {
        SelenideElement deleteButton = Grid.GridData.get().getRow(1).getDeleteButton();
        deleteButton.should(exist);
        deleteButton.should(visible);
        deleteButton.should(enabled);
    }
}
