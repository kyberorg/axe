package ee.yals.test.telegram;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Testing telegram bot auto configuration
 *
 * @since 2.5
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@Slf4j
public class TelegramAutoConfigTest extends TelegramTest {

    @Test
    public void sendStartCommandGivesNonEmptyReply() {
        if (isCorrectlyInitialized()) {
            String message = "hello";
            String reply = sendMessageToBot(message);
            assertNotNull(reply);
            assertNotEquals(EMPTY_RESPONSE, reply);
        } else {
            log.info("No token - no action");
        }
    }
}
