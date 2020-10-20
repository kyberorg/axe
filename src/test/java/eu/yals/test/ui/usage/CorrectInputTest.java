package eu.yals.test.ui.usage;

import eu.yals.test.ui.SelenideTest;
import eu.yals.test.ui.pageobjects.uus.HomePageObject;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ErrorModal.ERROR_MODAL;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ErrorModal.ERROR_TEXT;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.MainArea.LONG_URL_INPUT;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.QrCodeArea.QR_CODE;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.QrCodeArea.QR_CODE_AREA;
import static eu.yals.test.ui.pageobjects.uus.HomePageObject.ResultArea.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CorrectInputTest extends SelenideTest {
    @Before
    public void beforeTest() {
        open("/");
        updateTestNameHook();
    }

    @Test
    public void httpLink() {
        String link = "http://http.yadev.eu";
        HomePageObject.pasteValueInFormAndSubmitIt(link);
        checkExpectedBehavior();
    }

    private void checkExpectedBehavior() {
        RESULT_AREA.shouldBe(visible);
        RESULT_LINK.shouldBe(visible);
        RESULT_LINK.shouldHave(text(BASE_URL));
        COPY_LINK_ICON.shouldBe(visible);

        String actualText = RESULT_LINK.getText();
        String hrefValue = RESULT_LINK.getAttribute("href");
        assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

        QR_CODE_AREA.shouldBe(visible);
        QR_CODE.shouldBe(visible);
        assertTrue(QR_CODE.isImage());

        LONG_URL_INPUT.shouldBe(empty);

        ERROR_MODAL.shouldNotBe(visible);
        ERROR_TEXT.shouldBe(empty);
    }
}
