package pm.axe.test.pageobjects;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static pm.axe.test.pageobjects.VaadinPageObject.waitForVaadin;

public class DebugPageObject {
    public static final SelenideElement END_SESSION_BUTTON = $("#endSessionButton");

    public static void openDebugPage() {
        Selenide.open("/debug");
        waitForVaadin();
    }

    /**
     * Cleans current session by clicking {@link #END_SESSION_BUTTON}.
     */
    public static void cleanSession() {
        END_SESSION_BUTTON.click();
    }
}
