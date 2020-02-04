package eu.yals.test.ui.vaadin.pages.home;

import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.vaadin.HomePageTest;
import eu.yals.test.ui.vaadin.pageobjects.NotFoundViewPageObject;
import eu.yals.test.ui.vaadin.pageobjects.external.VR;
import eu.yals.test.utils.elements.VaadinElement;
import eu.yals.test.utils.elements.YalsElement;
import org.junit.Test;

/**
 * Testing /(Slash) URL
 *
 * @since 1.0
 */
public class HomePageTestIT extends HomePageTest {

  @Test
  public void urlWithJustSlashWillOpenFrontPage() {
    openHomePage();

    $$(homeView.getInput()).shouldExist();
    $$(homeView.getSubmitButton()).shouldExist();
  }

  @Test
  public void saveLinkAndClickOnResult() {
    openHomePage();

    homeView.pasteValueInFormAndSubmitIt("https://vr.fi");

    TestBenchElement shortLink = homeView.getShortLink();

    $$(shortLink).shouldBeDisplayed();
    shortLink.click();

    verifyThatVROpened();
  }

  @Test
  public void saveLinkAndCopyValueAndOpenIt() {
    openHomePage();

    homeView.pasteValueInFormAndSubmitIt("https://vr.fi");
    $$(homeView.getShortLink()).shouldBeDisplayed();
    String shortUrl = homeView.getShortLink().getText();

    open(shortUrl);
    verifyThatVROpened();
  }

  @Test
  public void openSomethingNonExisting() {
    open("/perkele");
    verifyThatPage404Opened();
  }

  @Test
  public void openSomethingNonExistingDeeperThanSingleLevel() {
    open("/void/something/here");
    verifyThatPage404Opened();
  }

  private void verifyThatVROpened() {
    YalsElement logo = $$(VR.LOGO);
    logo.shouldHaveAttr("alt", "VR");
  }

  private void verifyThatPage404Opened() {
    NotFoundViewPageObject page404 = NotFoundViewPageObject.getPageObject(getDriver());
    VaadinElement title = $$(page404.getTitle());

    title.shouldBeDisplayed();
    title.textHas("404");
  }
}
