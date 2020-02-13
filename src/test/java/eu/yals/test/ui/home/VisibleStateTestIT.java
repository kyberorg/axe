package eu.yals.test.ui.home;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import eu.yals.test.ui.HomePageTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * Checks state of front page (elements and so on...)
 *
 * @since 1.0
 */
public class VisibleStateTestIT extends HomePageTest {
  @Test
  public void errorBlockIsHidden() {
    openHomePage();
    assertThatErrorNotificationIsNotVisible();
  }

  @Test
  public void mainBlockIsVisible() {
    openHomePage();
    $$(homeView.getMainArea()).shouldBeDisplayed();
  }

  @Test(expected = NoSuchElementException.class)
  public void resultBlockIsHidden() {
    openHomePage();
    $$(homeView.getResultArea()).shouldNotBeDisplayed();
  }

  @Test(expected = NoSuchElementException.class)
  public void qrCodeBlockIsHidden() {
    openHomePage();
    $$(homeView.getQRCodeArea()).shouldNotBeDisplayed();
  }

  @Test
  public void mainAreaHasFieldAndButton() {
    openHomePage();
    $$(homeView.getInput()).shouldExist();
    $$(homeView.getSubmitButton()).shouldExist();
  }

  @Test
  public void formHasOnlyOneButton() {
    openHomePage();
    List<ButtonElement> buttons = homeView.getMainArea().$(ButtonElement.class).all();
    Assert.assertEquals("Only 1 button expected", 1, buttons.size());
  }

  @Test
  public void inputAndButtonAreNotDisabled() {
    openHomePage();
    $$(homeView.getInput()).shouldBeEnabled();
    $$(homeView.getSubmitButton()).shouldBeEnabled();
  }

  @Test
  public void inputShouldHavePlaceholder() {
    openHomePage();
    $$(homeView.getInput()).shouldHaveAttr("placeholder");
  }

  @Test
  public void shouldHaveCorrectTitle() {
    openHomePage();
    String title = getDriver().getTitle();
    Assert.assertEquals("Link shortener for friends", title);
  }

  @Test
  public void mainDivShouldHaveH2() {
    openHomePage();
    $$(homeView.getTitle()).shouldExist();
  }

  @Test
  public void inputFieldHasLabel() {
    openHomePage();
    String label = homeView.getInput().getLabel();
    Assert.assertTrue(StringUtils.isNotBlank(label));
  }

  @Test
  public void buttonIsPrimaryAndHasText() {
    openHomePage();
    ButtonElement button = homeView.getSubmitButton();
    $$(button).shouldHaveAttr("theme", "primary");
    $$(button).shouldHaveText("Shorten it!");
  }

  @Test
  public void publicAccessBannerIsPresentAndHasNeededText() {
    openHomePage();
    $$(homeView.getPublicAccessBanner()).shouldExist();
    $$(homeView.getPublicAccessBanner()).shouldBeDisplayed();
    $$(homeView.getPublicAccessBanner()).shouldHaveText("public");
  }

  @Test
  public void overallLinksDivExistsAndDisplayed() {
    openHomePage();
    $$(homeView.getOverallArea()).shouldExist();
    $$(homeView.getOverallArea()).shouldBeDisplayed();
  }

  @Test
  public void overallLinksTextExistsAndDisplayed() {
    openHomePage();
    $$(homeView.getOverallLinksText()).shouldExist();
    $$(homeView.getOverallLinksText()).shouldBeDisplayed();
    $$(homeView.getOverallLinksText()).shouldHaveText("Yals already saved");
  }

  @Test
  public void overallLinksNumberExistsAndNumber() {
    openHomePage();
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
