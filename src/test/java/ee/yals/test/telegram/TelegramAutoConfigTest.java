package ee.yals.test.telegram;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import ee.yals.App;
import ee.yals.Endpoint;
import ee.yals.json.TelegramStatusResponseJson;
import ee.yals.test.utils.TestUtils;
import ee.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;


/**
 * Testing telegram bot auto configuration
 *
 * @since 2.5
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@Slf4j
public class TelegramAutoConfigTest {

    @Test
    public void sendStartCommandGivesNonEmptyReply() {
        if (TestUtils.isLocalRun()) {
            log.info("Local run: telegram bot might not be started. Safe to ignore");
        }

        String statusApiEndpoint = String.format("%s%s", System.getProperty(App.Properties.TEST_URL),
                Endpoint.TELEGRAM_STATUS_API);
        HttpResponse<String> apiResponse;
        try {
            apiResponse = Unirest.get(statusApiEndpoint).asString();
        } catch (Exception e) {
            log.error("Failed to Request API. Communication error", e);
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
