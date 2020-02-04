package eu.yals.test.utils.elements;

import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import org.junit.Assert;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

public class VaadinElement extends YalsElement {
  private TestBenchElement testBenchElement;

  public static VaadinElement wrap(TestBenchElement element) {
    return new VaadinElement(element);
  }

  public VaadinElement(TestBenchElement element) {
    super(element);
    this.testBenchElement = element;
  }

  public void inputShouldBeEmpty() {
    Assert.assertNotNull("No such element found", testBenchElement);
    Assert.assertTrue("Input is not TextField", testBenchElement instanceof TextFieldElement);

    TextFieldElement element = (TextFieldElement) testBenchElement;
    Assert.assertTrue("Input is not empty", StringUtils.isBlank(element.getValue()));
  }
}
