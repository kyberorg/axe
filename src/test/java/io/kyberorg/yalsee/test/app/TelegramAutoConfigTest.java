package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.json.TelegramStatusResponseJson;
import io.kyberorg.yalsee.test.TestUtils;
import io.kyberorg.yalsee.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_200;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Testing telegram bot auto configuration.
 *
 * @since 2.5
 */
@SuppressWarnings({"unchecked", "RawTypeCanBeGeneric", "rawtypes"})
@Slf4j
public class TelegramAutoConfigTest extends UnirestTest {
    public static final String TAG = "[" + TelegramAutoConfigTest.class.getSimpleName() + "]";

    /**
     * Tests that /start command to Telegram Bot replied with non empty reply.
     */
    @Test
    public void sendStartCommandGivesNonEmptyReply() {
        if (TestUtils.isLocalRun()) {
            log.info(
                    "Local run: telegram bot won't be started without token ENV {}. Safe to ignore",
                    App.Env.TELEGRAM_TOKEN);
            return;
        }

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.TELEGRAM_STATUS_API);
        HttpResponse<String> apiResponse = request.asString();

        logRequestAndResponse(request, apiResponse, TAG);

        if (apiResponse.getStatus() == STATUS_200) {
            String status = extractBotStatus(apiResponse);
            assertTrue(status.contains("Online"));
        } else {
            String message = String.format("API Request failed with status %s", apiResponse.getStatus());
            log.error(message);
            fail(message);
        }
    }

    private String extractBotStatus(final HttpResponse<String> apiResponse) {
        TelegramStatusResponseJson json =
                AppUtils.GSON.fromJson(apiResponse.getBody(), TelegramStatusResponseJson.class);
        return json.getStatus();
    }
}
