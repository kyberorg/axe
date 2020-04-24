package eu.yals.test.ui.pageobjects;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.elements.ClipboardHelperElement;
import eu.yals.ui.HomeView;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for {@link HomeView}. Contains elements from HomeView.
 *
 * @since 2.7
 */
@Slf4j
public class HomeViewPageObject extends YalsPageObject {
    private static final int MILLIS_IN_SECOND = 1000;

    /**
     * Provides {@link HomeViewPageObject}.
     *
     * @param driver web driver
     * @return page object
     */
    public static HomeViewPageObject getPageObject(final WebDriver driver) {
        return new HomeViewPageObject(driver);
    }

    /**
     * Creates {@link HomeViewPageObject}.
     *
     * @param driver web driver
     */
    public HomeViewPageObject(final WebDriver driver) {
        super(driver, HomeView.IDs.VIEW_ID);
    }

    /**
     * Pastes link to input.
     *
     * @param link string with link
     */
    public void pasteValueInForm(String link) {
        getInput().setValue(link);
    }

    /**
     * Pastes link and click button.
     *
     * @param link string with link.
     */
    public void pasteValueInFormAndSubmitIt(String link) {
        pasteValueInForm(link);
        getSubmitButton().click();
    }

    /**
     * Text of short link.
     *
     * @return string with short link
     */
    public String getSavedUrl() {
        return getShortLink().getText();
    }

    /**
     * Number of links.
     *
     * @return long with number
     */
    public long getNumberOfSavedLinks() {
        long linksCount;
        try {
            linksCount = Long.parseLong(getOverallLinksNumber().getText());
        } catch (NumberFormatException e) {
            linksCount = 0;
        }
        return linksCount;
    }

    /**
     * Main area.
     *
     * @return element
     */
    public VerticalLayoutElement getMainArea() {
        return $(VerticalLayoutElement.class).id(HomeView.IDs.MAIN_AREA);
    }

    /**
     * Title.
     *
     * @return element
     */
    public H2Element getTitle() {
        return getMainArea().$(H2Element.class).id(HomeView.IDs.TITLE);
    }

    /**
     * Input.
     *
     * @return element
     */
    public TextFieldElement getInput() {
        return getMainArea().$(TextFieldElement.class).id(HomeView.IDs.INPUT);
    }

    /**
     * Banner.
     *
     * @return element
     */
    public SpanElement getPublicAccessBanner() {
        return getMainArea().$(SpanElement.class).id(HomeView.IDs.BANNER);
    }

    /**
     * Submit button.
     *
     * @return element
     */
    public ButtonElement getSubmitButton() {
        return getMainArea().$(ButtonElement.class).id(HomeView.IDs.SUBMIT_BUTTON);
    }

    /**
     * Overall area.
     *
     * @return element
     */
    public TestBenchElement getOverallArea() {
        return $(TestBenchElement.class).id(HomeView.IDs.OVERALL_AREA);
    }

    /**
     * Text of overall links.
     *
     * @return element
     */
    public SpanElement getOverallLinksText() {
        return getOverallArea().$(SpanElement.class).id(HomeView.IDs.OVERALL_LINKS_TEXT);
    }

    /**
     * Text of overall links number.
     *
     * @return element
     */
    public SpanElement getOverallLinksNumber() {
        return getOverallArea().$(SpanElement.class).id(HomeView.IDs.OVERALL_LINKS_NUMBER);
    }

    /**
     * Result area.
     *
     * @return element
     */
    public TestBenchElement getResultArea() {
        return $(TestBenchElement.class).id(HomeView.IDs.RESULT_AREA);
    }

    /**
     * Short link.
     *
     * @return element
     */
    public AnchorElement getShortLink() {
        return getResultArea().$(AnchorElement.class).id(HomeView.IDs.SHORT_LINK);
    }

    /**
     * Copy link button.
     *
     * @return element
     */
    public ClipboardHelperElement getCopyLinkButton() {
        return getResultArea().$(ClipboardHelperElement.class).id(HomeView.IDs.COPY_LINK_BUTTON);
    }

    /**
     * QR Area
     *
     * @return element
     */
    public TestBenchElement getQRCodeArea() {
        return $(TestBenchElement.class).id(HomeView.IDs.QR_CODE_AREA);
    }

    /**
     * QR code.
     *
     * @return element
     */
    public ImageElement getQRCode() {
        return getQRCodeArea().$(ImageElement.class).id(HomeView.IDs.QR_CODE);
    }

    /**
     * Error Notification.
     *
     * @return element.
     */
    public NotificationElement getErrorNotification() {
        sleep(2); // waiting notification to appear
        return $(NotificationElement.class).onPage().first();
    }

    @SuppressWarnings("SameParameterValue")
    private void sleep(final long seconds) {
        try {
            Thread.sleep(seconds * MILLIS_IN_SECOND);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
