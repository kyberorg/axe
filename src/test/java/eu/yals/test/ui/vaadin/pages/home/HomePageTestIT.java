package eu.yals.test.ui.vaadin.pages.home;

import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
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
public class HomePageTestIT extends VaadinTest {

  protected HomeViewPageObject getHomePage() {
    return HomeViewPageObject.getPageObject(getDriver());
  }

  private HomeViewPageObject openHomePage() {
    open("/");
    return getHomePage();
  }

  @Test
  public void urlWithJustSlashWillOpenFrontPage() {
    HomeViewPageObject homePage = openHomePage();

    $$(homePage.getInput()).shouldExist();
    $$(homePage.getSubmitButton()).shouldExist();
  }

  @Test
  public void saveLinkAndClickOnResult() {
    HomeViewPageObject homePage = openHomePage();

    homePage.pasteValueInFormAndSubmitIt("https://vr.fi");

    TestBenchElement shortLink = homePage.getShortLink();

    $$(shortLink).shouldBeDisplayed();
    shortLink.click();

    verifyThatVROpened();
  }

  @Test
  public void saveLinkAndCopyValueAndOpenIt() {
    HomeViewPageObject homePage = openHomePage();

    homePage.pasteValueInFormAndSubmitIt("https://vr.fi");
    $$(homePage.getShortLink()).shouldBeDisplayed();
    String shortUrl = homePage.getShortLink().getText();

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
