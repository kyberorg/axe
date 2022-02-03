package io.kyberorg.yalsee.test.ui.mylinks;

import io.kyberorg.yalsee.test.pageobjects.HomePageObject;
import io.kyberorg.yalsee.test.pageobjects.NotFoundViewPageObject;
import io.kyberorg.yalsee.test.pageobjects.elements.CookieBannerPageObject;
import io.kyberorg.yalsee.test.ui.SelenideTest;
import io.kyberorg.yalsee.ui.components.DeleteConfirmationDialog;
import io.kyberorg.yalsee.ui.err.PageNotFoundView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.Issue;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.kyberorg.yalsee.test.pageobjects.MyLinksViewPageObject.*;
import static io.kyberorg.yalsee.test.pageobjects.VaadinPageObject.waitForVaadin;

/**
 * Tests {@link DeleteConfirmationDialog} by performing different actions with its elements.
 *
 * @since 3.10
 */
@Issue("https://github.com/kyberorg/yalsee/issues/680")
public class DeleteConfirmationDialogActionsTest extends SelenideTest {

    /**
     * Test Setup.
     */
    @BeforeEach
    public void beforeEachTest() {
        //cleaning session
        openMyLinksPage();
        CookieBannerPageObject.closeBannerIfAny();
        cleanSession();
        waitForVaadin(); //this is needed to prevent unopened page after reload.
        CookieBannerPageObject.closeBannerIfAny(); //banner re-appears for new session
    }

    /**
     * Cancel Button just closes dialog - Record is not deleted.
     */
    @Test
    public void cancelButtonClosesDialog_and_RecordRemainsUntouched() {
        saveLinkAndReturnToMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(1));

        clickFirstDeleteButton();

        DeleteDialog.DIALOG.shouldBe(visible);
        DeleteDialog.CANCEL_BUTTON.click();

        DeleteDialog.DIALOG.shouldNotBe(visible);
        Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    /**
     * Escape just closes dialog - Record is not deleted.
     */
    @Test
    public void escapeClosesDialog_and_RecordRemainsUntouched() {
        saveLinkAndReturnToMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(1));

        clickFirstDeleteButton();

        DeleteDialog.DIALOG.shouldBe(visible);
        $("body").pressEscape();

        DeleteDialog.DIALOG.shouldNotBe(visible);
        Grid.GridData.get().getDataRows().shouldHave(size(1));
    }

    /**
     * Tests that Delete Button - opens Dialog and Dialog's Delete button really deletes Record,
     * so link is no longer in system and {@link PageNotFoundView} appears.
     */
    @Test
    public void deleteButtonDeletesRecord() {
        HomePageObject.saveOneLink("https://kyberorg.io");
        String shortUrl = HomePageObject.getSavedUrl();

        openMyLinksPage();

        Grid.GridData.get().getDataRows().shouldHave(size(1));
        clickFirstDeleteButton();

        DeleteDialog.DIALOG.shouldBe(visible);
        DeleteDialog.DELETE_BUTTON.click();

        DeleteDialog.DIALOG.shouldNotBe(visible);

        //reload needed - because Vaadin just hides element in grid
        openMyLinksPage();
        Grid.GridData.get().getDataRows().shouldHave(size(0));

        open(shortUrl);

        NotFoundViewPageObject.TITLE.shouldBe(visible);
        NotFoundViewPageObject.TITLE.shouldHave(text("404"));
    }

    private void saveLinkAndReturnToMyLinksPage() {
        HomePageObject.saveOneLink("https://kyberorg.io");
        openMyLinksPage();
    }
}
