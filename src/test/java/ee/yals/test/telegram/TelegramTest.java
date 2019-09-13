package ee.yals.test.telegram;

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
    private static final String NO_BOT_NAME_MARKER = "dummyBot";
    private static final String NO_BOT_TOKEN_MARKER = "dummyToken";
    static final String EMPTY_RESPONSE = "_void_";

    @Value("${test.telegram.botname}")
    private String botName;

    @Value("${test.telegram.token}")
    private String botToken;

    String sendMessageToBot(String message) {
        if (StringUtils.isBlank(message)) {
            throw new IllegalArgumentException("You asked me send nothing to bot. Are you stupid ?");
        }
        if (!isCorrectlyInitialized()) {
            log.warn("Cannot connect to Telegram with this bot name {} and/or token {}", botName, botToken);
        }
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, botToken, botName, message);

        String response = EMPTY_RESPONSE;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            response = sb.toString();
        } catch (MalformedURLException e) {
            log.error("Internal error: Malformed Telegram API URL", e);
        } catch (IOException e) {
            log.error("I/O error: Cannot open contact telegram bot. Do you have Internet access?");
        }
        return response;
    }

    boolean isCorrectlyInitialized() {
        boolean allSet = true;
        if (StringUtils.isBlank(botName) || botName.equals(NO_BOT_NAME_MARKER)) {
            log.warn("Bot Name: not set. Do you have '-Dtelegram.botname=' in your run command ?");
            allSet = false;
        }
        if (StringUtils.isBlank(botToken) || botToken.equals(NO_BOT_TOKEN_MARKER)) {
            log.warn("Bot Name: not set. Do you have '-Dtelegram.token=' in your run command ?");
            allSet = false;
        }
        return allSet;
    }

}
