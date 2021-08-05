package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionsWithOneLinkSavedTest extends SelenideTest {
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

        //saving one link
        open("/");
        VaadinPageObject.waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://kyberorg.io");

        //doing to page
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
    }

    @Test
    public void onItemClickItemDetailsOpened() {
        SelenideElement item = Grid.GridData.get().getRow(1).getDescriptionCell();
        item.click();
        SelenideElement itemDetails = Grid.GridData.get().getRow(1).getItemDetails();
        itemDetails.should(exist);
        itemDetails.shouldBe(visible);
    }

    @Test
    public void onItemClickItemDetailsOpenedAndHaveAllNeededElementsInside() {
        SelenideElement item = Grid.GridData.get().getRow(1).getDescriptionCell();
        item.click();
        SelenideElement itemDetailsElement = Grid.GridData.get().getRow(1).getItemDetails();
        Grid.GridItem.Details itemDetails = Grid.GridItem.Details.of(itemDetailsElement);

        //long link
        SelenideElement longLink = itemDetails.getLongLink();
        longLink.should(exist);
        longLink.shouldBe(visible);
        assertEquals("a", longLink.getTagName());
        String longLinkText = longLink.getText();
        longLink.shouldHave(attribute("href", longLinkText));

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
    }
}
