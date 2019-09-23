package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.yals.constants.App;
import eu.yals.controllers.rest.MattermostRestController;
import eu.yals.json.internal.Json;
import eu.yals.mm.Mattermost;
import eu.yals.utils.UrlExtraValidator;
import lombok.Getter;
import org.apache.commons.validator.GenericValidator;

/**
 * {@link MattermostRestController} outgoing JSON
 *
 * @since 2.3
 */
public class MattermostResponseJson extends Json {

    @Getter
    @JsonProperty("icon_url")
    private String iconUrl = App.Mattermost.BOT_ICON;

    @Getter
    @JsonProperty("text")
    private String text;

    @Getter
    @JsonProperty("response_type")
    private String responseType = Mattermost.ResponseType.IN_CHANNEL.toString();

    @Getter
    @JsonProperty("goto_location")
    private String gotoLocation;

    @Getter
    @JsonProperty("username")
    private final String username = App.Mattermost.BOT_NAME;

    private MattermostResponseJson() {
    }

    public static MattermostResponseJson createWithText(String text) {
        MattermostResponseJson mmJson = new MattermostResponseJson();

        boolean containsUrl = UrlExtraValidator.isStringContainsUrl(text);
        boolean isErrorMessage = text.contains(App.Emoji.WARNING);
        boolean isUsageMessage = (text.contains(App.Emoji.INFO) && text.contains("Usage"));
        if (containsUrl || isErrorMessage || isUsageMessage) {
            mmJson.text = text;
            return mmJson;
        } else {
            throw new IllegalArgumentException("Text must be either: valid URL, usage or error message");
        }
    }

    @SuppressWarnings("UnusedReturnValue") //by design
    public MattermostResponseJson replaceIconWith(String iconUrl) {
        if (GenericValidator.isUrl(iconUrl)) {
            this.iconUrl = iconUrl;
        } else {
            throw new IllegalArgumentException("Replacing Icon URL should be valid URL");
        }
        return this;
    }

    public MattermostResponseJson addGotoLocation(String gotoLocation) {
        if (GenericValidator.isUrl(gotoLocation)) {
            this.gotoLocation = gotoLocation;
        } else {
            throw new IllegalArgumentException("Goto location should be valid URL");
        }
        return this;
    }

    public MattermostResponseJson setResponseTypeTo(Mattermost.ResponseType responseType) {
        this.responseType = responseType.toString();
        return this;
    }

}
