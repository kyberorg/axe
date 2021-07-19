package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Telegram Status outgoing JSON.
 *
 * @since 2.5
 */
@Data
@RequiredArgsConstructor(staticName = "createWithStatus")
public class TelegramStatusResponse implements YalseeJson {
    @JsonProperty("status")
    private final String status;

    @JsonProperty("name")
    private String name;

    /**
     * Adds bot name.
     *
     * @param botName non-empty string with valid name for bot
     * @return json with stored bot name
     */
    public TelegramStatusResponse withBotName(final String botName) {
        this.name = botName;
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
