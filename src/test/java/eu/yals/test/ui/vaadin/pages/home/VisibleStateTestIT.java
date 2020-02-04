package eu.yals.test.ui.vaadin.pages.home;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.util.List;

import static org.junit.Assert.fail;

public class VisibleStateTestIT extends VaadinTest {
  private HomeViewPageObject homeView;

  public void openUrl() {
    open("/");
    homeView = HomeViewPageObject.getPageObject(getDriver());
  }

  @Test
  public void errorBlockIsHidden() {
    openUrl();
    // FIXME impl
  }

  @Test
  public void mainBlockIsVisible() {
    openUrl();
    $$(homeView.getMainArea()).shouldBeDisplayed();
  }

  @Test
  public void resultBlockIsHidden() {
    openUrl();
    $$(homeView.getResultArea()).shouldNotBeDisplayed();
  }

  @Test
  public void qrCodeBlockIsHidden() {
    openUrl();
    $$(homeView.getQRCodeArea()).shouldNotBeDisplayed();
  }

  @Test
  public void mainAreaHasFieldAndButton() {
    openUrl();
    $$(homeView.getInput()).shouldExist();
    $$(homeView.getSubmitButton()).shouldExist();
  }

  @Test
  public void formHasOnlyOneButton() {
    openUrl();
    List<ButtonElement> buttons = homeView.getMainArea().$(ButtonElement.class).all();
    Assert.assertEquals("Only 1 button expected", 1, buttons.size());
  }

  @Test
  public void inputAndButtonAreNotDisabled() {
    openUrl();
    $$(homeView.getInput()).shouldNotBeDisabled();
    $$(homeView.getSubmitButton()).shouldNotBeDisabled();
  }

  @Test
  public void inputShouldHavePlaceholder() {
    openUrl();
    $$(homeView.getInput()).shouldHaveAttr("placeholder");
  }

  @Test
  public void shouldHaveCorrectTitle() {
    openUrl();
    String title = getDriver().getTitle();
    Assert.assertEquals("Link shortener for friends", title);
  }

  @Test
  public void mainDivShouldHaveH2() {
    openUrl();
    $$(homeView.getTitle()).shouldExist();
  }

  @Test
  public void inputFieldHasLabel() {
    openUrl();
    String label = homeView.getInput().getLabel();
    Assert.assertTrue(StringUtils.isNotBlank(label));
  }

  @Test
  public void buttonIsPrimaryAndHasText() {
    openUrl();
    ButtonElement button = homeView.getSubmitButton();
    $$(button).shouldHaveAttr("theme", "primary");
    $$(button).shouldHaveText("Shorten it!");
  }

  @Test
  public void publicAccessBannerIsPresentAndHasNeededText() {
    openUrl();
    $$(homeView.getPublicAccessBanner()).shouldExist();
    $$(homeView.getPublicAccessBanner()).shouldBeDisplayed();
    $$(homeView.getPublicAccessBanner()).shouldHaveText("public");
  }

  @Test
  public void overallLinksDivExistsAndDisplayed() {
    openUrl();
    $$(homeView.getOverallArea()).shouldExist();
    $$(homeView.getOverallArea()).shouldBeDisplayed();
  }

  @Test
  public void overallLinksTextExistsAndDisplayed() {
    openUrl();
    $$(homeView.getOverallLinksText()).shouldExist();
    $$(homeView.getOverallLinksText()).shouldBeDisplayed();
    $$(homeView.getOverallLinksText()).shouldHaveText("Yals already saved");
  }

  @Test
  public void overallLinksNumberExistsAndNumber() {
    openUrl();
    $$(homeView.getOverallLinksNumber()).shouldExist();
    $$(homeView.getOverallLinksNumber()).shouldBeDisplayed();
    String numberText = homeView.getOverallLinksNumber().getText();
    try {
      int numberOfSavedLinks = Integer.parseInt(numberText);
      Assert.assertTrue(numberOfSavedLinks >= 0);
    } catch (NumberFormatException e) {
      fail("Number of saved links is not a valid number");
    }
  }
}
