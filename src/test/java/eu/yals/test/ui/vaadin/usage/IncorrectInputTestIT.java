package eu.yals.test.ui.vaadin.usage;

import eu.yals.test.ui.vaadin.HomePageTest;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

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
    // TODO notification should have text CANNOT_EMPTY_TEXT
  }

  @Test
  public void singleSpace() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt(" ");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO notification should have text CANNOT_EMPTY_TEXT
  }

  @Test
  public void twoSpaces() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("  ");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO notification should have text CANNOT_EMPTY_TEXT
  }

  @Test
  public void shortVariantOfNotUrlInput() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("g&%g");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO FrontPage.ErrorRow.ERROR_TEXT.shouldHave(and("short and notURL text",
    // text(MALFORMED_URL_TEXT)));
  }

  @Test
  public void longVariantOfNotUrlInput() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("veryLongStringWhichIsNotURL%&");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
  }

  @Test
  public void urlWithSpacesShallNotPass() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("http://site with spaces.com");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
  }

  @Test
  public void urlWithSpecialCharsShallNotPass() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("http://f%&k.com");

    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text(MALFORMED_URL_TEXT));
  }

  @Test
  public void urlWithBadProtocolShallNotPass() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("file:///etc/passwd");
    formIsClearedResultAndQRCodeAreNotVisible();
    errorBoxShouldAppear();
    // TODO FrontPage.ErrorRow.ERROR_TEXT.shouldHave(text("protocol not supported"));
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
    // TODO error Notification should be visible
    // TODO error text should not be empty
    // TODO error close buttom should be visible
  }
}
