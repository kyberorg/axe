package ee.yals.test.selenide.front;


import com.codeborne.selenide.SelenideElement;
import ee.yals.test.selenide.UITest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Checks state of front page (elements and so on...)
 *
 * @since 1.0
 */
public class VisibleState extends UITest {

    @Before
    public void openUrl() {
        open("/");
    }

    @Test
    public void errorBlockIsHidden() {
        $("#error").shouldNotBe(visible);
    }

    @Test
    public void mainBlockIsVisible() {
        $("#main").shouldBe(visible);
    }

    @Test
    public void resultBlockIsHidden() {
        $("#result").shouldNotBe(visible);
    }

    @Test
    public void formHasFieldAndButton() {
        SelenideElement formField = $("form").find("input#longUrl");

        formField.shouldBe(exist);
        formField.shouldHave(type("text"));

        SelenideElement button = $("form").find("button");
        button.shouldBe(exist);
    }

    @Test
    public void formHasOnlyOneButton() {
        $("form").findAll("button").shouldHaveSize(1);
    }

    @Test
    public void inputAndButtonAreNotDisabled() {
        $("form").find("input#longUrl").shouldNotBe(disabled);
        $("form").find("button").shouldNotBe(disabled);
    }

    @Test
    public void inputShouldHavePlaceholder() {
        $("form").find("input#longUrl").shouldHave(attribute("placeholder"));
    }

    @Test
    public void shouldHaveCorrectTitle() {
        String title = title();
        Assert.assertEquals("Link shortener for friends", title);
    }

    @Test
    public void mainDivShouldHaveH2() {
        $("#main h2").shouldBe(exist);
    }

    @Test
    public void inputFieldHasLabel() {
        SelenideElement label = $("#longUrl").parent().find("label");
        label.shouldBe(exist);
        label.shouldNotBe(empty);
        label.shouldHave(attribute("for", "longUrl"));
    }

    @Test
    public void buttonIsPrimaryAndHasText() {
        $("form").find("button").has(cssClass("btn-primary")); //This class make button blue
        $("form").find("button").shouldHave(text("Shorten it!"));
    }
}
