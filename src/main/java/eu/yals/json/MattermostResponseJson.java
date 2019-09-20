package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.yals.controllers.rest.MattermostRestController;
import eu.yals.json.internal.Json;
import eu.yals.mm.Mattermost;
import eu.yals.mm.Mattermost.Emoji;
import eu.yals.utils.UrlExtraValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * {@link MattermostRestController} outcoming JSON
 *
 * @since 2.3
 */
public class MattermostResponseJson extends Json {

    @JsonProperty("icon_url")
    private String iconUrl = Mattermost.Constants.BOT_ICON;

    @JsonProperty("text")
    private String text;

    @JsonProperty("response_type")
    private String responseType = Mattermost.ResponseType.IN_CHANNEL.toString();

    @JsonProperty("goto_location")
    private String gotoLocation;

    @JsonProperty("username")
    private String username = Mattermost.Constants.BOT_NAME;

    private MattermostResponseJson() {
    }

    public static MattermostResponseJson createWithText(String text) {
        MattermostResponseJson mmJson = new MattermostResponseJson();

        boolean containsUrl = UrlExtraValidator.isStringContainsUrl(text);
        boolean isErrorMessage = text.contains(Emoji.WARNING);
        boolean isUsageMessage = (text.contains(Emoji.INFO) && text.contains("Usage"));
        if (containsUrl || isErrorMessage || isUsageMessage) {
            mmJson.text = text;
            return mmJson;
        } else {
            throw new IllegalArgumentException("Text must be either: valid URL, usage or error message");
        }
    }

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

    public String getText() {
        return text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getGotoLocation() {
        return gotoLocation;
    }

    public String getUsername() {
        return username;
    }



}
