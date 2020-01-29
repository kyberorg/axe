package eu.yals.test.utils.elements;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

public class YalsElement {
  private WebElement webElement;

  public static YalsElement wrap(WebElement element) {
    return new YalsElement(element);
  }

  protected YalsElement(WebElement element) {
    this.webElement = element;
  }

  public void shouldHaveAttr(String attributeName, String exceptedValue) {
    Assert.assertNotNull("No such element found", webElement);

    String attributeValue = webElement.getAttribute(attributeName);
    Assert.assertNotNull(String.format("No such attribute '%s'", attributeName), attributeValue);
    Assert.assertTrue("Attribute is empty", StringUtils.isNotBlank(attributeValue));

    Assert.assertEquals(exceptedValue, attributeValue);
  }

  public void shouldExist() {
    Assert.assertNotNull("No such element found", webElement);
  }

  public void shouldBeDisplayed() {
    shouldExist();
    Assert.assertTrue(webElement.isDisplayed());
  }

  public void textHas(String phrase) {
    shouldExist();
    Assert.assertTrue(
        String.format("Text: '%s' has no '%s'", webElement.getText(), phrase),
        webElement.getText().contains(phrase));
  }
}
