package eu.yals.test.ui.vaadin.usage;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import eu.yals.test.ui.vaadin.HomePageTest;
import eu.yals.test.ui.vaadin.pageobjects.DebugViewPageObject;
import eu.yals.test.ui.vaadin.pageobjects.external.Wikipedia;
import eu.yals.test.utils.elements.YalsElement;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Contains multi step tests for Front page
 *
 * @since 1.0
 */
public class MultiStepTestIT extends HomePageTest {
  @Test
  public void closeButtonReallyClosesErrorNotification() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt(" ");
    Assert.assertTrue(homeView.getErrorNotification().isOpen());
    ButtonElement closeButton = homeView.getErrorNotification().$(ButtonElement.class).first();
    closeButton.click();
    assertThatErrorNotificationIsNotVisible();
  }

  @Test
  public void shortenItButtonClearsResultAndValueIfVisible() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

    $$(homeView.getResultArea()).shouldBeDisplayed();
    $$(homeView.getInput()).inputShouldBeEmpty();

    homeView.pasteValueInFormAndSubmitIt("g&%g");
    $$(homeView.getResultArea()).shouldNotBeDisplayed();
    $$(homeView.getShortLink()).shouldBeEmpty();
  }

  //@Test not working yet (https://vaadin.com/forum/thread/18090427/paste-from-clipboard-not-working)
  public void copyLinkButtonShouldCopyShortLink() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");

    $$(homeView.getResultArea()).shouldBeDisplayed();
    $$(homeView.getCopyLinkButton()).shouldBeDisplayed();

    homeView.getCopyLinkButton().contextClick();
    homeView.getCopyLinkButton().click();
    homeView.getInput().click();
    homeView.getInput().sendKeys(Keys.chord(Keys.CONTROL, "v"));

    String shortLink = homeView.getSavedUrl();
    String pastedLink = homeView.getInput().getValue();

    Assert.assertEquals(shortLink, pastedLink);
  }

  @Test
  public void linksCounterIncreasedValueAfterSave() {
    openHomePage();
    long initialNumber = homeView.getNumberOfSavedLinks();

    homeView.pasteValueInFormAndSubmitIt("https://github.com/yadevee/yals");
    long numberAfterLinkSaved = homeView.getNumberOfSavedLinks();

    Assert.assertEquals(initialNumber + 1, numberAfterLinkSaved);
  }

  @Test
  public void saveAndRetrieveLinkFromRussianWikipedia() {
    openHomePage();
    homeView.pasteValueInFormAndSubmitIt(
        "https://ru.wikipedia.org/wiki/%D0%94%D0%B5%D0%BF%D0%BE%D1%80%D1%82%D0%B0%D1%86%D0%B8%D0%B8_%D0%B8%D0%B7_%D0%AD%D1%81%D1%82%D0%BE%D0%BD%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D0%B2%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B9_%D0%A1%D0%BE%D1%86%D0%B8%D0%B0%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B9_%D0%A0%D0%B5%D1%81%D0%BF%D1%83%D0%B1%D0%BB%D0%B8%D0%BA%D0%B8");

    open(homeView.getSavedUrl());

    YalsElement articleTitle = $$(Wikipedia.getArticleTitle());
    articleTitle.shouldExist();
    articleTitle.shouldHaveText(Wikipedia.ARTICLE_TITLE);
  }

  //@Test //not working yet (https://vaadin.com/forum/thread/18090427/paste-from-clipboard-not-working)
  public void debugIssue() throws IOException, UnsupportedFlavorException {
    open("/debug");
    DebugViewPageObject debugViewPageObject = DebugViewPageObject.getPageObject(getDriver());
    debugViewPageObject.getButton().click();
    debugViewPageObject.getInput().click();
    String clipboardContent = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor); // extracting the text that was copied to the clipboard
    debugViewPageObject.getInput().sendKeys(clipboardContent);

    String excepted = "some stuff";
    String actual = debugViewPageObject.getInput().getValue();

    Assert.assertEquals(excepted, actual);
  }
}
