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
 * Page Object for {@link HomeView}. Contains elements from HomeView
 *
 * @since 2.7
 */
@Slf4j
public class HomeViewPageObject extends YalsPageObject {

  public static HomeViewPageObject getPageObject(WebDriver driver) {
    return new HomeViewPageObject(driver);
  }

  public HomeViewPageObject(WebDriver driver) {
    super(driver, HomeView.IDs.VIEW_ID);
  }

  public void pasteValueInFormAndSubmitIt(String link) {
    getInput().setValue(link);
    getSubmitButton().click();
  }

  public String getSavedUrl() {
    return getShortLink().getText();
  }

  public long getNumberOfSavedLinks() {
    long linksCount;
    try {
      linksCount = Long.parseLong(getOverallLinksNumber().getText());
    } catch (NumberFormatException e) {
      linksCount = 0;
    }
    return linksCount;
  }

  public VerticalLayoutElement getMainArea() {
    return $(VerticalLayoutElement.class).id(HomeView.IDs.MAIN_AREA);
  }

  public H2Element getTitle() {
    return getMainArea().$(H2Element.class).id(HomeView.IDs.TITLE);
  }

  public TextFieldElement getInput() {
    return getMainArea().$(TextFieldElement.class).id(HomeView.IDs.INPUT);
  }

  public SpanElement getPublicAccessBanner() {
    return getMainArea().$(SpanElement.class).id(HomeView.IDs.BANNER);
  }

  public ButtonElement getSubmitButton() {
    return getMainArea().$(ButtonElement.class).id(HomeView.IDs.SUBMIT_BUTTON);
  }

  public TestBenchElement getOverallArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.OVERALL_AREA);
  }

  public SpanElement getOverallLinksText() {
    return getOverallArea().$(SpanElement.class).id(HomeView.IDs.OVERALL_LINKS_TEXT);
  }

  public SpanElement getOverallLinksNumber() {
    return getOverallArea().$(SpanElement.class).id(HomeView.IDs.OVERALL_LINKS_NUMBER);
  }

  public TestBenchElement getResultArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.RESULT_AREA);
  }

  public AnchorElement getShortLink() {
    return getResultArea().$(AnchorElement.class).id(HomeView.IDs.SHORT_LINK);
  }

  public ClipboardHelperElement getCopyLinkButton() {
    return getResultArea().$(ClipboardHelperElement.class).id(HomeView.IDs.COPY_LINK_BUTTON);
  }

  public TestBenchElement getQRCodeArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.QR_CODE_AREA);
  }

  public ImageElement getQRCode() {
    return getQRCodeArea().$(ImageElement.class).id(HomeView.IDs.QR_CODE);
  }

  public NotificationElement getErrorNotification() {
    sleep(2); // waiting notification to appear
    return $(NotificationElement.class).onPage().first();
  }

  @SuppressWarnings("SameParameterValue")
  private void sleep(long seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }
  }
}
