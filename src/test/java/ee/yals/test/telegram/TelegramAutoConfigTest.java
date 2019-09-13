package ee.yals.test.telegram;

import ee.yals.telegram.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

import static org.junit.Assert.assertNotNull;


/**
 * Testing telegram bot auto configuration
 *
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class TelegramAutoConfigTest {

    @Autowired
    private TelegramBot bot;

    @Test
    public void sendStartCommandGivesNonEmptyReply() {
        boolean isBotRegistered = Objects.nonNull(bot) && !bot.getBotToken().equals(TelegramBot.DUMMY_TOKEN);
        if (isBotRegistered) {
            SendMessage message = new SendMessage();
            message.setText("/start").setChatId("123");
            try {
                Object reply = bot.execute(message);
                log.info("Reply object: " + reply.toString());
                assertNotNull(reply);
            } catch (TelegramApiException e) {
                log.error("Could not sent message", e);
            }
        } else {
            log.info("No token - no action");
        }
    }
}
