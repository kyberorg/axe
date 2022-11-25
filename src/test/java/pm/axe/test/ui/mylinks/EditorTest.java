package pm.axe.test.ui.mylinks;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.axe.test.TestApp;
import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.pages.mylinks.MyLinksPage;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests Grid Editor at {@link MyLinksPage}.
 *
 * @since 3.2
 */
public class EditorTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        //session cleanup
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
        MyLinksViewPageObject.cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.

        //saving one link
        open("/");
        VaadinPageObject.waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://kyberorg.io");

        //doing to page
        open("/myLinks");
        VaadinPageObject.waitForVaadin();
    }

    /**
     * Tests that Description is updated immediately after Editor closes.
     */
    @Test
    public void descriptionShouldBeUpdatedImmediately() {
        String newDescription = "New Description";
        SelenideElement descriptionCell =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    /**
     * Tests that updated Description stay even after Page reloaded.
     */
    @Test
    public void descriptionStayAfterPageReload() {
        String newDescription = "Description should stay";
        SelenideElement descriptionCell =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        //reload page
        open("/myLinks");
        VaadinPageObject.waitForVaadin();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    /**
     * Tests that Editor edits only one Record.
     */
    @Test
    public void editorShouldEditOnlyOneRecord() {
        //saving one more link
        open("/");
        VaadinPageObject.waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/kyberorg/axe/issues/195");

        //open MyLink page again
        open("/myLinks");
        VaadinPageObject.waitForVaadin();

        String newDescription = "The Description";
        SelenideElement descriptionCell =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));

        SelenideElement secondDescription =
                MyLinksViewPageObject.Grid.GridData.get().getRow(2).getDescriptionCell();
        secondDescription.shouldBe(empty);
    }

    /**
     * Tests that editor updates Record's Updated Time after Edit.
     */
    @Test
    public void updateTimeUpdatedAfterEdit() {
        String newDescription = "Testing update time";
        SelenideElement descriptionCell =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        Selenide.sleep(TestApp.Constants.TWO_SECONDS);

        SelenideElement descriptionEditor =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.click();
        SelenideElement itemDetailsElement =
                MyLinksViewPageObject.Grid.GridData.get().getRow(1).getItemDetails();
        if (!itemDetailsElement.isDisplayed()) {
            descriptionCell.click();
        }
        SelenideElement createdTimeSpan =
                MyLinksViewPageObject.Grid.GridItem.Details.of(itemDetailsElement).getCreatedTime();
        SelenideElement updatedTimeSpan =
                MyLinksViewPageObject.Grid.GridItem.Details.of(itemDetailsElement).getUpdatedTime();

        assertNotEquals(createdTimeSpan.getText(), updatedTimeSpan.getText());
    }

}
