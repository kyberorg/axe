package eu.yals.json;

import com.google.gson.annotations.Since;
import eu.yals.json.internal.Json;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Telegram Status outgoing JSON
 *
 * @since 2.5
 */
@Data
@RequiredArgsConstructor(staticName = "createWithStatus")
public class TelegramStatusResponseJson extends Json {
    @Since(2.5)
    private final String status;

    @Since(2.5)
    private String name;

    public TelegramStatusResponseJson withBotName(String botName) {
        this.name = botName;
        return this;
    }
}
