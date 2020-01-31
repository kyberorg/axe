package eu.yals.test.utils.elements;

import com.vaadin.flow.component.html.testbench.ImageElement;
import com.vaadin.testbench.TestBenchElement;

public class VaadinElement extends YalsElement {
  private TestBenchElement testBenchElement;

  public static VaadinElement wrap(TestBenchElement element) {
    return new VaadinElement(element);
  }

  public VaadinElement(TestBenchElement element) {
    super(element);
    this.testBenchElement = element;
  }

  @Override
  public boolean isImage() {
    return testBenchElement instanceof ImageElement;
  }
}
