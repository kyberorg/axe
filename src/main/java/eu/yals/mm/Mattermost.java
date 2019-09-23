package eu.yals.mm;

import eu.yals.constants.App;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**
 * Mattermost related code
 *
 * @since 2.3
 */
@Slf4j
public class Mattermost {
    private static final String TAG = "[MM]";

    private String channelId = App.NO_VALUE;
    private String channelName = App.NO_VALUE;
    private String command = App.NO_VALUE;
    private String teamDomain = App.NO_VALUE;
    private String teamId = App.NO_VALUE;
    private String text = App.NO_VALUE;
    private String token = App.NO_VALUE;
    private String userId = App.NO_VALUE;
    private String username = App.NO_VALUE;

    private MattermostArgumentSet argumentSet = MattermostArgumentSet.EMPTY_SET;

    private String param;

    private Mattermost(String body) {
        this.parseBody(body);
        this.parseText();
    }

    public static Mattermost createFromResponseBody(String body) {
        if (StringUtils.isBlank(body)) {
            throw new IllegalStateException("Body is missing");
        }

        Mattermost mm = new Mattermost(body);

        if (mm.argumentSet == MattermostArgumentSet.EMPTY_SET) {
            throw new NoSuchElementException("Got empty query (aka text) from MatterMost. Nothing to shorten");
        } else if (mm.argumentSet == MattermostArgumentSet.BROKEN_SET) {
            throw new IllegalArgumentException("Body must contain URL as first param");
        }
        return mm;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getCommand() {
        return command;
    }

    public String getTeamDomain() {
        return teamDomain;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getText() {
        return text;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public MattermostArgumentSet getArgumentSet() {
        return argumentSet;
    }

    private void parseBody(String body) {
        log.debug("{} Parsing body", TAG);
        String[] bodyParams = body.split("&");
        if (bodyParams.length == 0) {
            log.debug("{} Body has 0 params: nothing to do", TAG);
            return; //nothing to do
        }
        for (String bodyParam : bodyParams) {
            this.param = bodyParam;
            if (isParamStartWith(Marker.CHANNEL_ID)) {
                channelId = decodeText(removeMarker(Marker.CHANNEL_ID));
            } else if (isParamStartWith(Marker.CHANNEL_NAME)) {
                channelName = decodeText(removeMarker(Marker.CHANNEL_NAME));
            } else if (isParamStartWith(Marker.COMMAND)) {
                command = decodeText(removeMarker(Marker.COMMAND));
            } else if (isParamStartWith(Marker.TEAM_DOMAIN)) {
                teamDomain = decodeText(removeMarker(Marker.TEAM_DOMAIN));
            } else if (isParamStartWith(Marker.TEAM_ID)) {
                teamId = decodeText(removeMarker(Marker.TEAM_ID));
            } else if (isParamStartWith(Marker.TEXT)) {
                text = decodeText(removeMarker(Marker.TEXT));
            } else if (isParamStartWith(Marker.TOKEN)) {
                token = decodeText(removeMarker(Marker.TOKEN));
            } else if (isParamStartWith(Marker.USER_ID)) {
                userId = decodeText(removeMarker(Marker.USER_ID));
            } else if (isParamStartWith(Marker.USER_NAME)) {
                username = decodeText(removeMarker(Marker.USER_NAME));
            } else {
                log.debug("{} String '{}' doesn't match any pattern. Skipping...", TAG, this.param);
            }
        }
    }

    private String decodeText(String encodedString) {
        try {
            return URLDecoder.decode(encodedString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.warn("{} Failed to decode {}. Returning same encoded text", TAG, encodedString);
            return encodedString;
        }
    }

    private void parseText() {
        log.debug("{} Parsing text", TAG);
        if (StringUtils.isBlank(this.text) || this.text.equals(App.NO_VALUE)) {
            this.argumentSet = MattermostArgumentSet.builder().buildEmpty();
            return;
        }

        String[] arguments = this.text.split(" ");
        if (arguments.length == 0) {
            this.argumentSet = MattermostArgumentSet.builder().buildEmpty();
            return;
        }

        String url = arguments[0];
        String description = this.text.replace(url, "").trim();

        if (StringUtils.isNotBlank(description)) {
            this.argumentSet = MattermostArgumentSet.builderWithUrl(url).andDescription(description).build();
        } else {
            this.argumentSet = MattermostArgumentSet.builderWithUrl(url).build();
        }
    }

    private boolean isParamStartWith(Marker marker) {
        return param.startsWith(marker.value + App.EQUAL);
    }

    private String removeMarker(Marker marker) {
        return this.param.replace(marker.value + App.EQUAL, "");
    }

    public enum Marker {
        CHANNEL_ID("channel_id"),
        CHANNEL_NAME("channel_name"),
        COMMAND("command"),
        TEAM_DOMAIN("team_domain"),
        TEAM_ID("team_id"),
        TEXT("text"),
        TOKEN("token"),
        USER_ID("user_id"),
        USER_NAME("user_name");

        private final String value;

        Marker(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum ResponseType {
        EPHEMERAL("ephemeral"),
        IN_CHANNEL("in_channel");

        private final String value;

        ResponseType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
