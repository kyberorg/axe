package ee.yals.test.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import ee.yals.test.utils.Selenide;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Common stuff for all UI Test
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test-app.properties")
public abstract class UITest {

    protected final static String BASE_URL = "http://localhost:8080";

    @Rule // automatically takes screenshot of every failed test
    public ScreenShooter makeScreenShotsOnFail = ScreenShooter.failedTests()
            .to(System.getProperty(Selenide.Props.REPORT_DIR, Selenide.Defaults.REPORT_DIR));

    @BeforeClass
    public static void setUp() {
        Configuration.browser = System.getProperty(Selenide.Props.BROWSER, Selenide.Defaults.BROWSER);
        Configuration.timeout = Long.parseLong(System.getProperty(Selenide.Props.TIMEOUT, "5000"));
        Configuration.baseUrl = System.getProperty(Selenide.Props.BASE_URL, BASE_URL);
    }

    @After
    public void refresh() {
        com.codeborne.selenide.Selenide.refresh();
    }


    @AfterClass
    public static void closeBrowser() {
        com.codeborne.selenide.Selenide.close();
    }

}
