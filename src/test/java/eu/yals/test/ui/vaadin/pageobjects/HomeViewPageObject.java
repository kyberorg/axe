package eu.yals.test.ui.vaadin.pageobjects;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import eu.yals.ui.HomeView;
import org.openqa.selenium.WebDriver;

public class HomeViewPageObject extends YalsPageObject {

  public static HomeViewPageObject getPageObject(WebDriver driver) {
    return new HomeViewPageObject(driver);
  }

  public HomeViewPageObject(WebDriver driver) {
    super(driver, HomeView.IDs.VIEW_ID);
  }

  public void pasteValueInFormAndSubmitIt(String link) {
    getInputField().setValue(link);
    getSubmitButton().click();
  }

  public TestBenchElement getMainArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.MAIN_AREA);
  }

  public H2Element getTitleField() {
    return getMainArea().$(H2Element.class).id(HomeView.IDs.TITLE);
  }

  public SpanElement getSubtitleField() {
    return getMainArea().$(SpanElement.class).id(HomeView.IDs.SUBTITLE);
  }

  public TextFieldElement getInputField() {
    return getMainArea().$(TextFieldElement.class).id(HomeView.IDs.INPUT);
  }

  public SpanElement getPublicAccessBannerField() {
    return getMainArea().$(SpanElement.class).id(HomeView.IDs.BANNER);
  }

  public ButtonElement getSubmitButton() {
    return getMainArea().$(ButtonElement.class).id(HomeView.IDs.SUBMIT_BUTTON);
  }

  public TestBenchElement getOverallArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.OVERALL_AREA);
  }

  public SpanElement getOverallLinksTextField() {
    return getOverallArea().$(SpanElement.class).id(HomeView.IDs.OVERALL_LINKS_TEXT);
  }

  public SpanElement getOverallLinksNumber() {
    return getOverallArea().$(SpanElement.class).id(HomeView.IDs.OVERALL_LINKS_NUMBER);
  }

  public TestBenchElement getResultArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.RESULT_AREA);
  }

  public AnchorElement getShortLinkField() {
    return getResultArea().$(AnchorElement.class).id(HomeView.IDs.SHORT_LINK);
  }

  public ButtonElement getCopyLinkButton() {
    return getResultArea().$(ButtonElement.class).id(HomeView.IDs.COPY_LINK_BUTTON);
  }

  public TestBenchElement getQRCodeArea() {
    return $(TestBenchElement.class).id(HomeView.IDs.QR_CODE_AREA);
  }

  public ImageElement getQRCode() {
    return getQRCodeArea().$(ImageElement.class).id(HomeView.IDs.QR_CODE);
  }

  public TestBenchElement getFooter() {
    return $(TestBenchElement.class).id(HomeView.IDs.FOOTER);
  }

  public SpanElement getVersionField() {
    return getFooter().$(SpanElement.class).id(HomeView.IDs.VERSION);
  }

  public AnchorElement getCommitLink() {
    return getFooter().$(AnchorElement.class).id(HomeView.IDs.COMMIT_LINK);
  }
}
