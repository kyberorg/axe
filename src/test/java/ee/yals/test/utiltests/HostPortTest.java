package ee.yals.test.utiltests;

import ee.yals.test.YalsTest;
import ee.yals.utils.AppUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-app.properties")
public class HostPortTest extends YalsTest {
    @Test
    public void apiHostMethodProvidesCorrectInternalAPILocation() {
        String serverPortPropertyName = "server.port";
        String serverPort = "8080";
        String expectedHostPort = "localhost:8080";

        System.setProperty(serverPortPropertyName, serverPort);
        String actualHostPort = AppUtils.HostHelper.getAPIHostPort();

        assertEquals("Host Port Mismatch", expectedHostPort, actualHostPort);
    }
}
