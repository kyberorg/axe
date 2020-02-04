package eu.yals.test.ui.vaadin.usage;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import eu.yals.test.ui.vaadin.HomePageTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import static com.helger.commons.mock.CommonsAssert.fail;

public class IncorrectInputTestIT extends HomePageTest {
  private static final String CANNOT_EMPTY_TEXT = "cannot be empty";
  private static final String MALFORMED_URL_TEXT = "malformed URL or not URL";

  @Test
  public void emptyInput() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(CANNOT_EMPTY_TEXT);
  }

  @Test
  public void singleSpace() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt(" ");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(CANNOT_EMPTY_TEXT);
  }

  @Test
  public void twoSpaces() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("  ");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(CANNOT_EMPTY_TEXT);
  }

  @Test
  public void shortVariantOfNotUrlInput() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("g&%g");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(MALFORMED_URL_TEXT);
  }

  @Test
  public void longVariantOfNotUrlInput() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(MALFORMED_URL_TEXT);
  }

  @Test
  public void urlWithSpacesShallNotPass() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("http://site with spaces.com");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(MALFORMED_URL_TEXT);
  }

  @Test
  public void urlWithSpecialCharsShallNotPass() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("http://f%&k.com");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas(MALFORMED_URL_TEXT);
  }

  @Test
  public void urlWithBadProtocolShallNotPass() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("file:///etc/passwd");
    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    $$(homeView.getErrorNotification()).errorTextHas("protocol not supported");
  }

  private void formIsClearedResultAndQRCodeAreNotVisible() {
    $$(homeView.getInput()).inputShouldBeEmpty();
    try {
      $$(homeView.getResultArea());
      fail("Result Area should not visible");
    } catch (NoSuchElementException e) {
      // ok to be empty, because of strange dynamic logic of Vaadin TestBench
    }
    try {
      $$(homeView.getQRCodeArea());
      fail("QR Area should not visible");
    } catch (NoSuchElementException e) {
      // ok to be empty, because of strange dynamic logic of Vaadin TestBench
    }
  }

  private void errorBoxShouldAppear() {
    Assert.assertTrue(homeView.getErrorNotification().isOpen());
    Assert.assertTrue(StringUtils.isNotBlank(homeView.getErrorNotification().getText()));
    Assert.assertTrue(homeView.getErrorNotification().$(ButtonElement.class).exists());
  }
}
