package pm.axe.test.pageobjects;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import pm.axe.ui.pages.debug.DebugPage;

import static com.codeborne.selenide.Selenide.$;
import static pm.axe.test.pageobjects.VaadinPageObject.waitForVaadin;

public final class DebugPageObject {
    public static final SelenideElement END_SESSION_BUTTON = $("#endSessionButton");

    private DebugPageObject() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Opens {@link DebugPage}.
     */
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
