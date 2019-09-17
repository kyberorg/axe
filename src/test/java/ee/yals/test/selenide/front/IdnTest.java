package ee.yals.test.selenide.front;

import com.codeborne.selenide.SelenideElement;
import ee.yals.test.selenide.UITest;
import ee.yals.test.utils.pages.JosefssonOrg;
import ee.yals.test.utils.pages.KtoRf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.open;
import static ee.yals.test.utils.pages.FrontSelectors.ResultRow.RESULT_LINK;
import static org.junit.Assert.assertTrue;

/**
 * Contains IDN URL multi step tests for Front page
 *
 * @since 2.5
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IdnTest extends UITest {

    @Test
    public void russianUrl() {
        pasteValueInFormAndSubmitIt("http://кто.рф");

        String shortLink = RESULT_LINK.text();
        open(shortLink);
        verifyThatKtoRFOpened();
    }

    @Test
    public void swedishUrl() {
        pasteValueInFormAndSubmitIt("https://räksmörgås.josefsson.org");
        String shortLink = RESULT_LINK.text();
        open(shortLink);
        verifySwedishSiteOpened();
    }

    private void verifyThatKtoRFOpened() {
        SelenideElement eggs = KtoRf.DIV_EGGS;
        eggs.shouldBe(exist);
    }

    private void verifySwedishSiteOpened() {
        SelenideElement h1 = JosefssonOrg.H1;
        h1.should(exist);
        String h1Text = h1.text();
        assertTrue(h1Text.contains(JosefssonOrg.H1_TEXT));
    }
}
