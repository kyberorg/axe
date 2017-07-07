package ee.yals.test.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Common stuff for all UI Test
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class UITest {

    protected final static String BASE_URL = "http://localhost:8080";

    @BeforeClass
    public static void setUp() {
        Configuration.browser = System.getProperty("selenide.browser", "htmlunit");
        Configuration.timeout = Long.parseLong(System.getProperty("selenide.timeout", "5000"));
        Configuration.baseUrl = System.getProperty("selenide.baseUrl", BASE_URL);
    }

    @After
    public void refresh() {
        Selenide.refresh();
    }


    @AfterClass
    public static void closeBrowser() {
        Selenide.close();
    }

}
