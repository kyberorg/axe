package eu.yals.test.utils.elements;

import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import org.junit.Assert;
import org.openqa.selenium.By;
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
        String.format("Error text: %s does not contain phrase: %s", getNotificationText(notification), phrase),
        StringUtils.containsIgnoreCase(getNotificationText(notification), phrase));
  }

  private String getNotificationText(NotificationElement notification) {
    final String BUTTON_TEXT = "OK";
    String notificationText = notification.getText();
    if (!notificationText.equalsIgnoreCase(BUTTON_TEXT)) {
      return notificationText;
    }

    TestBenchElement notificationCard = notification.getPropertyElement("_card");
    TestBenchElement label =
            notificationCard
                    .findElement(By.tagName("vaadin-horizontal-layout"))
                    .findElement(By.tagName("label"));
    return label.getText();
  }
}
