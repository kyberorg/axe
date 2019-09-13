package ee.yals.test.telegram;

import ee.yals.test.utils.Selenide;
import ee.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Common methods for telegram bot testing
 *
 * @since 2.5
 */

@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
abstract class TelegramTest {

    boolean isLocalRun() {
        String testUrl = System.getProperty(Selenide.Props.TEST_URL, "");
        String dockerHost = "host.testcontainers.internal";
        String localhost = "localhost";

        return (testUrl.contains(dockerHost) || testUrl.contains(localhost));
    }

}
