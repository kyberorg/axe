package pm.axe.test.ui.mylinks;

import pm.axe.test.pageobjects.HomePageObject;
import pm.axe.test.pageobjects.NotFoundViewPageObject;
import pm.axe.test.pageobjects.elements.CookieBannerPageObject;
import pm.axe.test.ui.SelenideTest;
import pm.axe.ui.elements.DeleteConfirmationDialog;
import pm.axe.ui.pages.err.page404.PageNotFoundPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;
import pm.axe.test.pageobjects.MyLinksViewPageObject;
import pm.axe.test.pageobjects.VaadinPageObject;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Tests {@link DeleteConfirmationDialog} by performing different actions with its elements.
 *
 * @since 3.10
 */
@Issue("https://github.com/kyberorg/axe/issues/680")
public class DeleteConfirmationDialogActionsTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        //cleaning session
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.cleanSession();
        VaadinPageObject.waitForVaadin(); //this is needed to prevent unopened page after reload.
    }

    /**
     * Cancel Button just closes dialog - Record is not deleted.
     */
    @Test
    public void cancelButtonClosesDialog_and_RecordRemainsUntouched() {
        saveLinkAndReturnToMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));

        MyLinksViewPageObject.clickFirstDeleteButton();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldBe(visible);
        MyLinksViewPageObject.DeleteDialog.CANCEL_BUTTON.click();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldNotBe(visible);
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    /**
     * Escape just closes dialog - Record is not deleted.
     */
    @Test
    public void escapeClosesDialog_and_RecordRemainsUntouched() {
        saveLinkAndReturnToMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));

        MyLinksViewPageObject.clickFirstDeleteButton();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldBe(visible);
        $("body").pressEscape();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldNotBe(visible);
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    /**
     * Tests that Delete Button - opens Dialog and Dialog's Delete button really deletes Record,
     * so link is no longer in system and {@link PageNotFoundPage} appears.
     */
    @Test
    public void deleteButtonDeletesRecord() {
        HomePageObject.saveOneLink("https://kyberorg.io");
        String shortUrl = HomePageObject.getSavedUrl();

        MyLinksViewPageObject.openMyLinksPage();

        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(1));
        MyLinksViewPageObject.clickFirstDeleteButton();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldBe(visible);
        MyLinksViewPageObject.DeleteDialog.DELETE_BUTTON.click();

        MyLinksViewPageObject.DeleteDialog.DIALOG.shouldNotBe(visible);

        //reload needed - because Vaadin just hides element in grid
        MyLinksViewPageObject.openMyLinksPage();
        MyLinksViewPageObject.Grid.GridData.get().getDataRows().shouldHave(size(0));

        open(shortUrl);

        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
    }

    private void saveLinkAndReturnToMyLinksPage() {
        HomePageObject.saveOneLink("https://kyberorg.io");
        MyLinksViewPageObject.openMyLinksPage();
    }
}
