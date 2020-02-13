package eu.yals.test.ui.pageobjects;

import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import eu.yals.ui.DebugView;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for {@link DebugView}. Contains elements from DebugView
 *
 * @since 2.7
 */
public class DebugViewPageObject extends YalsPageObject {

  public static DebugViewPageObject getPageObject(WebDriver driver) {
    return new DebugViewPageObject(driver);
  }

  public DebugViewPageObject(WebDriver driver) {
    super(driver, DebugView.class.getSimpleName());
  }

  // elements
  public TestBenchElement getButton() {
    return getCl().$("vaadin-button").first();
  }

  public TextFieldElement getInput() {
    return $(TextFieldElement.class).first();
  }

  private TestBenchElement getCl() {
    return $("clipboard-helper").first();
  }
}
