package io.kyberorg.yalsee.test.ui.mylinks;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.test.utils.TestUtils;
import io.kyberorg.yalsee.ui.elements.DeleteConfirmationDialog;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.Condition.*;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Test default visual state of {@link DeleteConfirmationDialog}.
 */
@Issue("https://github.com/kyberorg/yalsee/issues/680")
public class DeleteConfirmationDialogVisualTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeAll
    public static void beforeAllTests() {
        openMyLinksPage();
        CookieBannerPageObject.closeBannerIfAny();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        CookieBannerPageObject.closeBannerIfAny(); //banner re-appears for new session

        HomePageObject.saveOneLink("https://kyberorg.io");
        openMyLinksPage();
        clickFirstDeleteButton();
    }

    /**
     * Dialog has Title.
     */
    @Test
    public void dialogHasTitle() {
        DeleteDialog.TITLE.should(exist);
        DeleteDialog.TITLE.shouldBe(visible);
    }

    /**
     * Dialog Title has Words "Confirm Delete".
     */
    @Test
    public void dialogTitleHasNeededWords() {
        DeleteDialog.TITLE.shouldHave(text("Confirm Delete"));
    }

    /**
     * Dialog has Message.
     */
    @Test
    public void dialogHasMessage() {
        DeleteDialog.MESSAGE.should(exist);
        DeleteDialog.MESSAGE.shouldBe(visible);
    }

    /**
     * Dialog Message has Words "sure" and "undone".
     */
    @Test
    public void dialogMessageHasNeededWords() {
        DeleteDialog.MESSAGE.shouldHave(text("sure"));
        DeleteDialog.MESSAGE.shouldHave(text("?"));
        DeleteDialog.MESSAGE.shouldHave(text("undone"));
    }

    /**
     * Dialog has Cancel Button.
     */
    @Test
    public void dialogHasCancelButton() {
        DeleteDialog.CANCEL_BUTTON.should(exist);
        DeleteDialog.CANCEL_BUTTON.shouldBe(visible);
    }

    /**
     * Cancel Button's Text is "Cancel".
     */
    @Test
    public void cancelButtonTextIsCancel() {
        DeleteDialog.CANCEL_BUTTON.shouldHave(text("Cancel"));
    }

    /**
     * Dialog has Delete Button.
     */
    @Test
    public void dialogHasDeleteButton() {
        DeleteDialog.DELETE_BUTTON.should(exist);
        DeleteDialog.DELETE_BUTTON.shouldBe(visible);
    }

    /**
     * Delete Button's Text is "Delete".
     */
    @Test
    public void deleteButtonTextIsDelete() {
        DeleteDialog.DELETE_BUTTON.shouldHave(text("Delete"));
    }

    /**
     * Delete Button has Danger/Error Theme.
     */
    @Test
    public void deleteButtonHasDangerTheme() {
        TestUtils.assertHasTheme(DeleteDialog.DELETE_BUTTON, "error");
    }

    /**
     * Cleanup after all tests.
     */
    @AfterAll
    public static void afterAllTests() {
        //cleanup
        DeleteDialog.DELETE_BUTTON.click();
    }
}
