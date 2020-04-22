package eu.yals.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.yals.constants.App;
import eu.yals.json.internal.Json;
import eu.yals.mm.Mattermost;
import eu.yals.utils.UrlExtraValidator;
import lombok.Getter;
import org.apache.commons.validator.GenericValidator;

/**
 * {@link eu.yals.controllers.rest.MattermostRestController} outgoing JSON.
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

    /**
     * Create response JSON with provided text
     *
     * @param text string with message text
     * @return JSON which sent to requester mattermost
     */
    public static MattermostResponseJson createWithText(final String text) {
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

    /**
     * Replaces default Icon in message with given one.
     *
     * @param urlWithIcon valid full URL with icon
     * @return same JSON but with replaced icon
     */
    @SuppressWarnings("UnusedReturnValue") //by design
    public MattermostResponseJson replaceIconWith(final String urlWithIcon) {
        if (GenericValidator.isUrl(urlWithIcon)) {
            this.iconUrl = urlWithIcon;
        } else {
            throw new IllegalArgumentException("Replacing Icon URL should be valid URL");
        }
        return this;
    }

    /**
     * Puts location.
     *
     * @param gotoLocation string contains gotoLocation.
     * @return json which senda in response
     */
    public MattermostResponseJson addGotoLocation(final String gotoLocation) {
        if (GenericValidator.isUrl(gotoLocation)) {
            this.gotoLocation = gotoLocation;
        } else {
            throw new IllegalArgumentException("Goto location should be valid URL");
        }
        return this;
    }

    /**
     * Modifies Mattermost response type.
     *
     * @param responseType valid {@link Mattermost.ResponseType}
     * @return same jon
     */
    public MattermostResponseJson setResponseTypeTo(final Mattermost.ResponseType responseType) {
        this.responseType = responseType.toString();
        return this;
    }

}
