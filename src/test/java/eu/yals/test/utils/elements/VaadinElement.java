package eu.yals.test.utils.elements;

import com.vaadin.flow.component.notification.testbench.NotificationElement;
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

  public void errorTextHas(String phrase) {
    Assert.assertNotNull("No such element found", testBenchElement);
    Assert.assertTrue(
        "Element is not NotificationElement", testBenchElement instanceof NotificationElement);

    NotificationElement notification = (NotificationElement) testBenchElement;
    Assert.assertTrue(
        String.format("Phrase: %s does not contain %s", phrase, notification.getText()),
        StringUtils.containsIgnoreCase(notification.getText(), phrase));
  }
}
