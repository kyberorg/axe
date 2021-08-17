package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.MyLinksView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.Grid;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests Grid Editor at {@link MyLinksView}.
 *
 * @since 3.2
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class EditorTest extends SelenideTest {
    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeTest() {
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
    }

    /**
     * Tests that Description is updated immediately after Editor closes.
     */
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

    /**
     * Tests that updated Description stay even after Page reloaded.
     */
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
        waitForVaadin();

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
        waitForVaadin();
        HomePageObject.pasteValueInFormAndSubmitIt("https://github.com/kyberorg/yalsee/issues/195");

        //open MyLink page again
        open("/myLinks");
        waitForVaadin();

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

    /**
     * Tests that editor updates Record's Updated Time after Edit.
     */
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
