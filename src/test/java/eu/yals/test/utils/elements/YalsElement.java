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

  public void attrValue(String attributeName, String exceptedValue) {
    Assert.assertNotNull("No such element found", webElement);

    String attributeValue = webElement.getAttribute(attributeName);
    Assert.assertNotNull(String.format("No such attribute '%s'", attributeName), attributeValue);
    Assert.assertTrue("Attribute is empty", StringUtils.isNotBlank(attributeValue));

    Assert.assertEquals(exceptedValue, attributeValue);
  }
}
