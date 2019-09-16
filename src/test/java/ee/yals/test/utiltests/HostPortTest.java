package ee.yals.test.utiltests;

import ee.yals.constants.App;
import ee.yals.utils.AppUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class HostPortTest {

    @Autowired
    private AppUtils appUtils;

    @Test
    public void apiHostMethodProvidesCorrectInternalAPILocation() {
        String serverPortPropertyName = App.Properties.SERVER_PORT;
        String serverPort = "8080";
        String expectedHostPort = "localhost:8080";

        System.setProperty(serverPortPropertyName, serverPort);
        String actualHostPort = appUtils.getAPIHostPort();

        assertEquals("Host Port Mismatch", expectedHostPort, actualHostPort);
    }
}
