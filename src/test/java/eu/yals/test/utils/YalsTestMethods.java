package eu.yals.test.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

public class YalsTestMethods {
  private WebElement webElement;


  public static YalsTestMethods fromWebElement(WebElement element) {
    return new YalsTestMethods(element);
  }

  protected YalsTestMethods(WebElement element) {
    this.webElement = element;
  }

  public void attr(String attributeName, String exceptedValue) {
    Assert.assertNotNull("No such element found", webElement);

    String attributeValue = webElement.getAttribute(attributeName);
    Assert.assertNotNull(String.format("No such attribute '%s'", attributeName), attributeValue);
    Assert.assertTrue("Attribute is empty", StringUtils.isNotBlank(attributeValue));

    Assert.assertEquals(exceptedValue, attributeValue);
  }
}
