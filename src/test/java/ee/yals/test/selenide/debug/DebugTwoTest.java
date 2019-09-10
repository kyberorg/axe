package ee.yals.test.selenide.debug;

import com.codeborne.selenide.Selenide;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.$;

public class DebugTwoTest {

    @BeforeClass
    public static void setUp() {
        Core.setUp();
    }

    @Test
    public void testFullUrl() throws InterruptedException {
        Selenide.open("https://dev.yals.eu");
        $("#longUrl").setValue("https://ci.yadev.eu/job/YalsGH/job/boot2/");
        Core.clickIt();
    }

    @Test
    public void testAbsolute() throws InterruptedException {
        Selenide.open("/");
        $("#longUrl").setValue("https://ci.yadev.eu/job/YalsGH/");
        Core.clickIt();
    }

    @AfterClass
    public static void tearDown() {
        Core.tearDown();
    }
}
