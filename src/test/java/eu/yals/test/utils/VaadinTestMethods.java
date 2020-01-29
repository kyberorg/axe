package eu.yals.test.utils;

import com.vaadin.testbench.TestBenchElement;
import org.junit.Assert;

public class VaadinTestMethods extends YalsTestMethods {
  private TestBenchElement testBenchElement;

  public static VaadinTestMethods fromVaadinElement(TestBenchElement element) {
    return new VaadinTestMethods(element);
  }

  public VaadinTestMethods(TestBenchElement element) {
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
