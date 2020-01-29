package eu.yals.test.utils.elements;

import com.vaadin.testbench.TestBenchElement;
import org.junit.Assert;

public class VaadinElement extends YalsElement {
  private TestBenchElement testBenchElement;

  public static VaadinElement wrap(TestBenchElement element) {
    return new VaadinElement(element);
  }

  public VaadinElement(TestBenchElement element) {
    super(element);
    this.testBenchElement = element;
  }

  public void shouldBeDisplayed() {
    Assert.assertNotNull("No such element", testBenchElement);
    Assert.assertTrue("", testBenchElement.isDisplayed());
  }

  public void textHas(String phrase) {
    Assert.assertNotNull("No such element", testBenchElement);
    Assert.assertTrue(
        String.format("Text: '%s' has no '%s'", testBenchElement.getText(), phrase),
        testBenchElement.getText().contains(phrase));
  }
}
