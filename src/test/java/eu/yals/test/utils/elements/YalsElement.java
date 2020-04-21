package eu.yals.test.utils.elements;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

/**
 * Test methods for Selenide Elements
 *
 * @since 2.7
 */
public class YalsElement {
  private final WebElement webElement;

  public static YalsElement wrap(WebElement element) {
    return new YalsElement(element);
  }

  protected YalsElement(WebElement element) {
    this.webElement = element;
  }

  public void shouldHaveAttr(String attributeName) {
    shouldHaveAttr(attributeName, "");
  }

  public void shouldHaveAttr(String attributeName, String exceptedValue) {
    Assert.assertNotNull("No such element found", webElement);

    String attributeValue = webElement.getAttribute(attributeName);
    Assert.assertNotNull(String.format("No such attribute '%s'", attributeName), attributeValue);

    if (StringUtils.isNotBlank(exceptedValue)) {
      Assert.assertTrue("Attribute is empty", StringUtils.isNotBlank(attributeValue));
      Assert.assertEquals(exceptedValue, attributeValue);
    }
  }

  public void shouldExist() {
    Assert.assertNotNull("No such element found", webElement);
  }

  public void shouldBeDisplayed() {
    shouldExist();
    Assert.assertTrue("Element is not displayed", webElement.isDisplayed());
  }

  public void shouldNotBeDisplayed() {
    shouldExist();
    Assert.assertFalse("Element is displayed", webElement.isDisplayed());
  }

  public void shouldBeEnabled() {
    shouldExist();
    Assert.assertTrue("Element is disabled", webElement.isEnabled());
  }

  public void textHas(String phrase) {
    shouldExist();
    Assert.assertTrue(
        String.format("Text: '%s' has no '%s'", webElement.getText(), phrase),
        webElement.getText().contains(phrase));
  }

  public void shouldNotBeEmpty() {
    shouldExist();
    Assert.assertTrue("Text is empty", StringUtils.isNotBlank(webElement.getText()));
  }

  public void shouldBeEmpty() {
    shouldExist();
    Assert.assertTrue("Text is not empty", StringUtils.isBlank(webElement.getText()));
  }

  public void shouldHaveText(String text) {
    shouldExist();
    Assert.assertNotNull("Text is empty", webElement.getText());
    Assert.assertTrue(
        "Text does not have " + text, StringUtils.containsIgnoreCase(webElement.getText(), text));
  }

  public void shouldNotHaveText(String textToAvoid) {
    shouldExist();
    Assert.assertNotNull("Text is empty", webElement.getText());
    Assert.assertFalse(
        "Text have " + textToAvoid,
        StringUtils.containsIgnoreCase(webElement.getText(), textToAvoid));
  }

  public boolean isImage() {
    return (webElement.getTagName().equalsIgnoreCase("img"));
  }
}
