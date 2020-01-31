package eu.yals.test.ui.vaadin.usage;

import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
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
    homeView.pasteValueInFormAndSubmitIt("http://кто.рф");

    openSavedUrl();

    // verify that KtoRF opened
    YalsElement eggs = $$(KtoRf.DIV_EGGS);
    eggs.shouldExist();
  }

  private void openSavedUrl() {
    String shortLink = homeView.getSavedUrl();
    open(shortLink);
  }
}
