package eu.yals.test.ui.vaadin;

import com.vaadin.testbench.TestBenchElement;
import eu.yals.test.ui.vaadin.commons.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import eu.yals.test.ui.vaadin.pageobjects.NotFoundViewPageObject;
import eu.yals.test.ui.vaadin.pageobjects.external.VR;
import eu.yals.test.utils.elements.VaadinElement;
import eu.yals.test.utils.elements.YalsElement;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;

public class SlashIT extends VaadinTest {

  protected HomeViewPageObject getHomePage() {
    return HomeViewPageObject.getPageObject(getDriver());
  }

  @Test
  public void urlWithJustSlashWillOpenFrontPage() {
    open("/");
    HomeViewPageObject homePage = getHomePage();

    $$(homePage.getInput()).shouldExist();
    $$(homePage.getSubmitButton()).shouldExist();
  }

  @Test
  public void saveLinkAndClickOnResult() {
    open("/");
    HomeViewPageObject homePage = getHomePage();

    homePage.pasteValueInFormAndSubmitIt("https://vr.fi");

    TestBenchElement shortLink = homePage.getShortLink();

    $$(shortLink).shouldBeDisplayed();
    shortLink.click();

    verifyThatVROpened();
  }

  @Test
  public void saveLinkAndCopyValueAndOpenIt() {
    open("/");
    HomeViewPageObject homePage = getHomePage();

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
