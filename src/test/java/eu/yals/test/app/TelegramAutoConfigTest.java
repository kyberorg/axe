package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.json.TelegramStatusResponseJson;
import eu.yals.test.TestUtils;
import eu.yals.utils.AppUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Testing telegram bot auto configuration
 *
 * @since 2.5
 */
@SuppressWarnings({"unchecked", "RawTypeCanBeGeneric", "rawtypes"})
@Slf4j
public class TelegramAutoConfigTest extends UnirestTest {
  public static final String TAG = "[" + TelegramAutoConfigTest.class.getSimpleName() + "]";

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

    if (apiResponse.getStatus() == 200) {
      String status = extractBotStatus(apiResponse);
      assertTrue(status.contains("Online"));
    } else {
      String message = String.format("API Request failed with status %s", apiResponse.getStatus());
      log.error(message);
      fail(message);
    }
  }

  private String extractBotStatus(HttpResponse<String> apiResponse) {
    TelegramStatusResponseJson json =
        AppUtils.GSON.fromJson(apiResponse.getBody(), TelegramStatusResponseJson.class);
    return json.getStatus();
  }
}
