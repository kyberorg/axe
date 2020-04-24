package eu.yals.json;

import eu.yals.json.internal.Json;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Telegram Status outgoing JSON.
 *
 * @since 2.5
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(staticName = "createWithStatus")
public class TelegramStatusResponseJson extends Json {
    private final String status;

    private String name;

    /**
     * Adds bot's name.
     *
     * @param botName non-empty string with valid name for bot
     * @return json with stored bot name
     */
    public TelegramStatusResponseJson withBotName(final String botName) {
        this.name = botName;
        return this;
    }
}
