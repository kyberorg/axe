package eu.yals.test.utils.elements;

import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;

@Slf4j
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
    String notificationText = getNotificationText(notification);
    Assert.assertTrue(
        String.format("Error text: %s does not contain phrase: %s", notificationText, phrase),
        StringUtils.containsIgnoreCase(notificationText, phrase));
  }

  private String getNotificationText(NotificationElement notification) {
    final String BUTTON_TEXT = "OK";
    String notificationText = notification.getText();
    if (!notificationText.equalsIgnoreCase(BUTTON_TEXT)) {
      return notificationText;
    }

    log.info(
        "Seems like notification.getText() method gave button's text. Getting text using JS methods instead");

    TestBenchElement notificationCard = notification.getPropertyElement("_card");
    TestBenchElement label =
        notificationCard
            .findElement(By.tagName("vaadin-horizontal-layout"))
            .findElement(By.tagName("label"));
    return label.getText();
  }
}
