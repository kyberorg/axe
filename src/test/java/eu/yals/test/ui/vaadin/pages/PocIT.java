package eu.yals.test.ui.vaadin.pages;

import eu.yals.test.ui.vaadin.VaadinTest;
import eu.yals.test.ui.vaadin.pageobjects.HomeViewPageObject;
import org.junit.Assert;
import org.junit.Test;

public class PocIT extends VaadinTest {
  protected HomeViewPageObject getHomeViewPageObject() {
    return HomeViewPageObject.getPageObject(getDriver());
  }

  @Test
  public void testVaadin() {
    HomeViewPageObject homeView = getHomeViewPageObject();

    String titleText = homeView.getTitle().getText();
    Assert.assertEquals("Yet another link shortener", titleText);
  }
}
