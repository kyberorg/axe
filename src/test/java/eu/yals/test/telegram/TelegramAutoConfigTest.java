package eu.yals.test.telegram;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.json.TelegramStatusResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Testing telegram bot auto configuration
 *
 * @since 2.5
 */
@Slf4j
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@ContextConfiguration
public class TelegramAutoConfigTest {

    @Test
    public void sendStartCommandGivesNonEmptyReply() {
        if (TestUtils.isLocalRun()) {
            log.info("Local run: telegram bot won't be started without token ENV {}. Safe to ignore",
                    App.Env.TELEGRAM_TOKEN);
            return;
        }

        String statusApiEndpoint = String.format("%s%s", TestUtils.getTestUrl(), Endpoint.TELEGRAM_STATUS_API);
        HttpResponse<String> apiResponse;
        try {
            apiResponse = Unirest.get(statusApiEndpoint).asString();
        } catch (Exception e) {
            log.error("Failed to Request API. Communication error", e);
            //MalformedURLException means configuration error
            assertFalse(e.getCause() instanceof MalformedURLException);
            return;
        }

        if (apiResponse.getStatus() == 200) {
            String status = extractBotStatus(apiResponse);
            assertTrue(status.contains("Online"));
        } else {
            log.error("Api Request failed with status {}", apiResponse.getStatus());
            log.debug("API response: {}", apiResponse);
        }
    }

    private String extractBotStatus(HttpResponse<String> apiResponse) {
        TelegramStatusResponseJson json = AppUtils.GSON.fromJson(apiResponse.getBody(), TelegramStatusResponseJson.class);
        return json.getStatus();
    }
}
