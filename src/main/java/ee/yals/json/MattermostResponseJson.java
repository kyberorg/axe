package ee.yals.json;

import com.google.gson.annotations.SerializedName;
import ee.yals.json.internal.Json;
import ee.yals.mm.Mattermost;
import ee.yals.mm.Mattermost.Emoji;
import ee.yals.utils.UrlExtraValidator;
import org.apache.commons.validator.GenericValidator;

/**
 * {@link ee.yals.controllers.rest.MattermostRestController} outcoming JSON
 *
 * @since 2.3
 */
public class MattermostResponseJson extends Json {

    @SerializedName("icon_url")
    private String iconUrl = Mattermost.Constants.BOT_ICON;

    @SerializedName("text")
    private String text;

    @SerializedName("response_type")
    private String responseType = Mattermost.ResponseType.IN_CHANNEL.toString();

    @SerializedName("goto_location")
    private String gotoLocation;

    @SerializedName("user_name")
    private String username = "YalsBot";

    private MattermostResponseJson() {
    }

    public static MattermostResponseJson createWithText(String text) {
        MattermostResponseJson mmJson = new MattermostResponseJson();

        boolean containsUrl = UrlExtraValidator.isStringContainsUrl(text);
        boolean isErrorMessage = text.contains(Emoji.WARNING);
        if (containsUrl || isErrorMessage) {
            mmJson.text = text;
            return mmJson;
        } else {
            throw new IllegalArgumentException("Text must be either: valid URL or error message");
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
