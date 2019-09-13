package ee.yals.json;

import com.google.gson.annotations.Since;
import ee.yals.json.internal.Json;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Telegram Status outgoing JSON
 *
 * @since 2.5
 */
@Data
@RequiredArgsConstructor(staticName = "createWith")
public class TelegramStatusResponseJson extends Json {
    @Since(2.5)
    private final String name;

    @Since(2.5)
    private final String status;


}
