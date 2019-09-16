package ee.yals.test.utiltests;

import ee.yals.configuration.AppInfo;
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

    @Autowired
    private AppInfo appInfo;

    @Test
    public void apiHostMethodProvidesCorrectInternalAPILocation() {
        int portApplicationWorksAt = appInfo.getPort();
        String expectedHostPort = String.format("localhost:%d", portApplicationWorksAt);

        String actualHostPort = appUtils.getAPIHostPort();

        assertEquals("Host Port Mismatch", expectedHostPort, actualHostPort);
    }
}
