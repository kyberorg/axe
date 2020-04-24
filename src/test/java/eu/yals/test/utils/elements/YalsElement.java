package eu.yals.test.utils.elements;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

/**
 * Test methods for Selenide Elements.
 *
 * @since 2.7
 */
public class YalsElement {
    private final WebElement webElement;

    /**
     * Wrapper for {@link WebElement}s.
     *
     * @param element {@link WebElement} to be wrapped
     * @return {@link YalsElement} with {@link WebElement}
     */
    public static YalsElement wrap(final WebElement element) {
        return new YalsElement(element);
    }

    protected YalsElement(final WebElement element) {
        this.webElement = element;
    }

    /**
     * Checks if element has given attribute.
     *
     * @param attributeName string of attribute name
     */
    public void shouldHaveAttr(final String attributeName) {
        shouldHaveAttr(attributeName, "");
    }

    /**
     * Checks if element has given attribute and it has expected value.
     *
     * @param attributeName string with attribute name
     * @param expectedValue string with attribute value
     */
    public void shouldHaveAttr(final String attributeName, final String expectedValue) {
        Assert.assertNotNull("No such element found", webElement);

        String attributeValue = webElement.getAttribute(attributeName);
        Assert.assertNotNull(String.format("No such attribute '%s'", attributeName), attributeValue);

        if (StringUtils.isNotBlank(expectedValue)) {
            Assert.assertTrue("Attribute is empty", StringUtils.isNotBlank(attributeValue));
            Assert.assertEquals(expectedValue, attributeValue);
        }
    }

    /**
     * Checks if element is exists (not null).
     */
    public void shouldExist() {
        Assert.assertNotNull("No such element found", webElement);
    }

    /**
     * Checks if element is displayed.
     */
    public void shouldBeDisplayed() {
        shouldExist();
        Assert.assertTrue("Element is not displayed", webElement.isDisplayed());
    }

    /**
     * Checks if element is not displayed.
     */
    public void shouldNotBeDisplayed() {
        shouldExist();
        Assert.assertFalse("Element is displayed", webElement.isDisplayed());
    }

    /**
     * Checks if element is enabled.
     */
    public void shouldBeEnabled() {
        shouldExist();
        Assert.assertTrue("Element is disabled", webElement.isEnabled());
    }

    /**
     * Checks if element contains given phrase.
     *
     * @param phrase string with text to check
     */
    public void textHas(final String phrase) {
        shouldExist();
        Assert.assertTrue(
                String.format("Text: '%s' has no '%s'", webElement.getText(), phrase),
                webElement.getText().contains(phrase));
    }

    /**
     * Checks that element's text is not blank.
     */
    public void textShouldNotBeEmpty() {
        shouldExist();
        Assert.assertTrue("Text is empty", StringUtils.isNotBlank(webElement.getText()));
    }

    /**
     * Checks that element's text is empty.
     */
    public void textShouldBeEmpty() {
        shouldExist();
        Assert.assertTrue("Text is not empty", StringUtils.isBlank(webElement.getText()));
    }

    /**
     * Checks if element contains given text (case-insensitive).
     *
     * @param text string with text to check
     */
    public void shouldHaveText(final String text) {
        shouldExist();
        Assert.assertNotNull("Text is empty", webElement.getText());
        Assert.assertTrue(
                "Text does not have " + text, StringUtils.containsIgnoreCase(webElement.getText(), text));
    }

    /**
     * Check if element do not contain given text (case-insensitive).
     *
     * @param textToAvoid string with text that should not be there.
     */
    public void shouldNotHaveText(final String textToAvoid) {
        shouldExist();
        Assert.assertNotNull("Text is empty", webElement.getText());
        Assert.assertFalse(
                "Text have " + textToAvoid,
                StringUtils.containsIgnoreCase(webElement.getText(), textToAvoid));
    }

    /**
     * Check if element is image.
     *
     * @return true - if element is image, false if not
     */
    public boolean isImage() {
        return (webElement.getTagName().equalsIgnoreCase("img"));
    }
}
