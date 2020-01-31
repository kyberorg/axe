package eu.yals.test.ui.vaadin.usage;

import eu.yals.test.TestUtils;
import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tries to input valid values and checks returned result
 *
 * @since 1.0
 */
public class CorrectInputTest extends VaadinTest {
  private HomeViewPageObject homeView;

  public void openUrl() {
    open("/");
    homeView = HomeViewPageObject.getPageObject(getDriver());
  }

  @Test
  public void httpLink() {
    openUrl();
    String link = "http://virtadev.net";
    homeView.pasteValueInFormAndSubmitIt(link);
    checkExpectedBehavior();
  }

  @Test
  public void httpsLink() {
    openUrl();
    String link = "https://github.com/virtalab";
    homeView.pasteValueInFormAndSubmitIt(link);
    checkExpectedBehavior();
  }

  @Test
  public void ftpLink() {
    openUrl();
    String link = "ftp://ftp.yandex.ru";
    homeView.pasteValueInFormAndSubmitIt(link);
    checkExpectedBehavior();
  }

  @Test
  public void cyrillicLink() {
    openUrl();
    String link = "http://президент.рф";
    homeView.pasteValueInFormAndSubmitIt(link);
    checkExpectedBehavior();
  }

  @Test
  public void linkWithoutProtocol() {
    openUrl();
    String link = "www.kv.ee/2992207";
    homeView.pasteValueInFormAndSubmitIt(link);
    checkExpectedBehavior();
  }

  @Test
  public void linkFromRussianWikipedia() {
    openUrl();
    String link =
        "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8";
    homeView.pasteValueInFormAndSubmitIt(link);
    checkExpectedBehavior();
  }

  private void checkExpectedBehavior() {
    $$(homeView.getResultArea()).shouldBeDisplayed();
    $$(homeView.getShortLink()).shouldBeDisplayed();
    $$(homeView.getShortLink()).shouldHaveText(BASE_URL);

    // TODO copy Result icon should be visible

    String actualText = homeView.getShortLink().getText();
    String hrefValue = homeView.getShortLink().getAttribute("href");
    assertEquals("link in 'href' value is not same as link shown text", actualText, hrefValue);

    $$(homeView.getQRCodeArea()).shouldBeDisplayed();
    $$(homeView.getQRCode()).shouldBeDisplayed();
    assertTrue("QR code is not image", $$(homeView.getQRCode()).isImage());

    TestUtils.assertEmpty("Log URL Input is not empty", homeView.getInput().getValue());

    // TODO error modal should not visible
    // TODO error text should be empty
  }
}
