package eu.yals.test.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@RunWith(SpringRunner.class)
public class SampleTest extends SelenideTest {

    @Test
    public void selenideWorks() {
        open("/");
        $("body").should(exist);
    }
}
