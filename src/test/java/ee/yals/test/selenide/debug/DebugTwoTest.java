package ee.yals.test.selenide.debug;

import com.codeborne.selenide.Selenide;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DebugTwoTest {

    @BeforeClass
    public static void setUp() {
        Core.setUp();
    }

    @Test
    public void testFullUrl() throws InterruptedException {
        Selenide.open("https://dev.yals.eu");
        Core.pasteValueInFormAndSubmitIt("https://ci.yadev.eu/job/YalsGH/job/boot2/");

    }

    @Test
    public void testAbsolute() throws InterruptedException {
        Selenide.open("/");
        Core.pasteValueInFormAndSubmitIt("https://ci.yadev.eu/job/YalsGH/");
    }

    @AfterClass
    public static void tearDown() {
        Core.tearDown();
    }
}
