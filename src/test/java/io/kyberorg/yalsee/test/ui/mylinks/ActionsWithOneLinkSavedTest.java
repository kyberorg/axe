package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;

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
        SelenideElement item = Grid.GridData.get().getDataRows().get(0);
        item.click();
        SelenideElement itemDetails = Grid.GridData.get().getRow(1).getItemDetails();
        itemDetails.should(exist);
        itemDetails.shouldBe(visible);
    }
}
