package io.kyberorg.yalsee.test.ui.mylinks;

import com.codeborne.selenide.SelenideElement;
import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.VaadinPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.cleanSession;

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
        SelenideElement descriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));
    }

    @Test
    public void descriptionStayAfterPageReload() {
        String newDescription = "Description should stay";
        SelenideElement descriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
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
        SelenideElement descriptionCell = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionCell();
        descriptionCell.doubleClick();

        SelenideElement descriptionEditor = MyLinksViewPageObject.Grid.GridData.get().getRow(1).getDescriptionEditor();
        descriptionEditor.setValue(newDescription);
        descriptionEditor.pressEnter();

        descriptionCell.shouldNotBe(empty);
        descriptionCell.shouldHave(text(newDescription));

        SelenideElement secondDescription = MyLinksViewPageObject.Grid.GridData.get().getRow(2).getDescriptionCell();
        secondDescription.shouldBe(empty);
    }


}
