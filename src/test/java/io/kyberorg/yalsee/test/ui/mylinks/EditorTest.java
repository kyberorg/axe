package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EditorTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
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
    }

    @Test
    public void descriptionShouldBeUpdatedImmediately() {
        String newDescription = "New Description";
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    @Test
    public void descriptionStayAfterPageReload() {
        String newDescription = "Description should stay";
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        //reload page
        open("/myLinks");
        VaadinPageObject.waitForVaadin();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    @Test
    public void editorShouldEditOnlyOneRecord() {
        //saving one more link
        open("/");
        VaadinPageObject.waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/kyberorg/yalsee/issues/195");

        //open MyLink page again
        open("/myLinks");
        VaadinPageObject.waitForVaadin();

        String newDescription = "The Description";
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));

        SelenideElement secondDescription = Grid.GridData.get().getRow(2).getDescriptionCell();
        secondDescription.shouldBe(empty);
    }

    @Test
    public void updateTimeUpdatedAfterEdit() {
        String newDescription = "Testing update time";
        SelenideElement descriptionCell = Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.click();
        SelenideElement itemDetailsElement = Grid.GridData.get().getRow(1).getItemDetails();
        SelenideElement createdTimeSpan = Grid.GridItem.Details.of(itemDetailsElement).getCreatedTime();
        SelenideElement updatedTimeSpan = Grid.GridItem.Details.of(itemDetailsElement).getUpdatedTime();

        assertNotEquals(createdTimeSpan.getText(), updatedTimeSpan.getText());
    }

}
