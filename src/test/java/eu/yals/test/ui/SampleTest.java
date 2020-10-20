package eu.yals.test.ui;

import com.codeborne.selenide.SelenideElement;
import eu.yals.test.utils.vaadin.elements.ButtonElement;
import eu.yals.test.utils.vaadin.elements.TextFieldElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@RunWith(SpringRunner.class)
public class SampleTest extends SelenideTest {

    @Before
    public void openPage() {
        System.out.println("Second Before aka OpenPage");
        open("/");
        updateTestNameHook();
    }

    @Test
    public void selenideWorks() {
        $("body").should(exist);
    }

    @Test
    public void doSomethingReal() {
        //type something in input and click button
        SelenideElement input = TextFieldElement.byCss("#longUrlInput").getInput();
        input.should(exist);
        input.click();
        input.setValue("SomeText");

        SelenideElement submitButton = ButtonElement.byCss("#submitButton").getButton();
        submitButton.should(exist);
        submitButton.click();
    }
}
