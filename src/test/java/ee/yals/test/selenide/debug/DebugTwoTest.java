package ee.yals.test.selenide.debug;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DebugTwoTest {

    @BeforeClass
    public static void setUp() {
        Core.setUp();
    }

    @Test
    public void testFullUrl() throws InterruptedException {
        Selenide.open(Configuration.baseUrl);
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
