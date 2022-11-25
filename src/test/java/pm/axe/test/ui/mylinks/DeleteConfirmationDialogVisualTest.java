package pm.axe.test.ui.mylinks;

import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.test.utils.TestUtils;
import pm.axe.ui.elements.DeleteConfirmationDialog;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.Condition.*;

/**
 * Test default visual state of {@link DeleteConfirmationDialog}.
 */
@Issue("https://github.com/kyberorg/axe/issues/680")
public class DeleteConfirmationDialogVisualTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.

        HomePageObject.saveOneLink("https://kyberorg.io");
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.clickFirstDeleteButton();
    }

    /**
     * Dialog has Title.
     */
    @Test
    public void dialogHasTitle() {
        MyLinksViewPageObject.DeleteDialog.TITLE.should(exist);
        MyLinksViewPageObject.DeleteDialog.TITLE.shouldBe(visible);
    }

    /**
     * Dialog Title has Words "Confirm Delete".
     */
    @Test
    public void dialogTitleHasNeededWords() {
        MyLinksViewPageObject.DeleteDialog.TITLE.shouldHave(text("Confirm Delete"));
    }

    /**
     * Dialog has Message.
     */
    @Test
    public void dialogHasMessage() {
        MyLinksViewPageObject.DeleteDialog.MESSAGE.should(exist);
        MyLinksViewPageObject.DeleteDialog.MESSAGE.shouldBe(visible);
    }

    /**
     * Dialog Message has Words "sure" and "undone".
     */
    @Test
    public void dialogMessageHasNeededWords() {
        MyLinksViewPageObject.DeleteDialog.MESSAGE.shouldHave(text("sure"));
        MyLinksViewPageObject.DeleteDialog.MESSAGE.shouldHave(text("?"));
        MyLinksViewPageObject.DeleteDialog.MESSAGE.shouldHave(text("undone"));
    }

    /**
     * Dialog has Cancel Button.
     */
    @Test
    public void dialogHasCancelButton() {
        MyLinksViewPageObject.DeleteDialog.CANCEL_BUTTON.should(exist);
        MyLinksViewPageObject.DeleteDialog.CANCEL_BUTTON.shouldBe(visible);
    }

    /**
     * Cancel Button's Text is "Cancel".
     */
    @Test
    public void cancelButtonTextIsCancel() {
        MyLinksViewPageObject.DeleteDialog.CANCEL_BUTTON.shouldHave(text("Cancel"));
    }

    /**
     * Dialog has Delete Button.
     */
    @Test
    public void dialogHasDeleteButton() {
        MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON.should(exist);
        MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON.shouldBe(visible);
    }

    /**
     * Delete Button's Text is "Delete".
     */
    @Test
    public void deleteButtonTextIsDelete() {
        MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON.shouldHave(text("Delete"));
    }

    /**
     * Delete Button has Danger/Error Theme.
     */
    @Test
    public void deleteButtonHasDangerTheme() {
        TestUtils.assertHasTheme(MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON, "error");
    }

    /**
     * Cleanup after all tests.
     */
    @AfterAll
    public static void afterAllTests() {
        //cleanup
        MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON.click();
    }
}
