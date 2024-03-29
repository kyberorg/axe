package pm.axe.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;
import org.apache.commons.validator.GenericValidator;
import pm.axe.Axe;
import pm.axe.api.mm.MattermostRestController;
import pm.axe.mm.Mattermost;
import pm.axe.utils.UrlExtraValidator;

/**
 * {@link MattermostRestController} outgoing JSON.
 *
 * @since 2.3
 */
@Data
public final class MattermostResponse implements AxeJson {

    @JsonProperty("icon_url")
    private String iconUrl = Axe.Mattermost.BOT_ICON;

    @JsonProperty("text")
    private String text;

    @JsonProperty("response_type")
    private String responseType = Mattermost.ResponseType.IN_CHANNEL.toString();

    @JsonProperty("goto_location")
    private String gotoLocation;

    @JsonProperty("username")
    private final String username = Axe.Mattermost.BOT_NAME;

    private MattermostResponse() {
    }

    /**
     * Create response JSON with provided text.
     *
     * @param text string with message text
     * @return JSON which sent to requester mattermost
     */
    public static MattermostResponse createWithText(final String text) {
        MattermostResponse mmJson = new MattermostResponse();

        boolean containsUrl = UrlExtraValidator.isStringContainsUrl(text);
        boolean isErrorMessage = text.contains(Axe.Emoji.WARNING);
        boolean isUsageMessage = (text.contains(Axe.Emoji.INFO) && text.contains("Usage"));
        if (containsUrl || isErrorMessage || isUsageMessage) {
            mmJson.text = text;
            return mmJson;
        } else {
            throw new IllegalArgumentException("Text must be either: valid URL, usage or error message");
        }
    }

    /**
     * Replaces default Icon in message with given one.
     *
     * @param iconUrl valid full URL with icon
     * @return same JSON but with replaced icon
     */
    @SuppressWarnings("UnusedReturnValue") //by design
    public MattermostResponse replaceIconWith(final String iconUrl) {
        if (GenericValidator.isUrl(iconUrl)) {
            this.iconUrl = iconUrl;
        } else {
            throw new IllegalArgumentException("Replacing Icon URL should be valid URL");
        }
        return this;
    }

    /**
     * Puts location.
     *
     * @param gotoLocation string contains gotoLocation.
     * @return json which sends in response
     */
    public MattermostResponse addGotoLocation(final String gotoLocation) {
        if (GenericValidator.isUrl(gotoLocation)) {
            this.gotoLocation = gotoLocation;
        } else {
            throw new IllegalArgumentException("Goto location should be valid URL");
        }
        return this;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
