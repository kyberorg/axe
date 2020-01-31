package eu.yals.test.ui.vaadin.usage;

import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import eu.yals.test.ui.vaadin.pageobjects.external.JosefssonOrg;
import eu.yals.test.ui.vaadin.pageobjects.external.KtoRf;
import eu.yals.test.utils.elements.YalsElement;
import org.junit.Test;

/**
 * Contains IDN URL multi step tests for Front page
 *
 * @since 2.5
 */
public class IdnTestIT extends VaadinTest {
  private HomeViewPageObject homeView;

  public void openUrl() {
    open("/");
    homeView = HomeViewPageObject.getPageObject(getDriver());
  }

  @Test
  public void russianUrl() {
    openUrl();
    homeView.pasteValueInFormAndSubmitIt("http://кто.рф");

    openSavedUrl();

    // verify that KtoRF opened
    YalsElement eggs = $$(KtoRf.DIV_EGGS);
    eggs.shouldExist();
  }

  @Test
  public void swedishUrl() {
    openUrl();
    homeView.pasteValueInFormAndSubmitIt("https://räksmörgås.josefsson.org");
    openSavedUrl();

    //verify that swedish site opened
    YalsElement h1 = $$(JosefssonOrg.H1);
    h1.shouldExist();
    h1.textHas(JosefssonOrg.H1_TEXT);
  }

  private void openSavedUrl() {
    String shortLink = homeView.getSavedUrl();
    open(shortLink);
  }
}
