package eu.yals.mm;

import eu.yals.constants.App;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.NoSuchElementException;

import static eu.yals.mm.Mattermost.Constants.NO_VALUE;

/**
 * Mattermost related code
 *
 * @since 2.3
 */
@Slf4j
public class Mattermost {
    private static final String TAG = "[MM]";

    private String channelId = NO_VALUE;
    private String channelName = NO_VALUE;
    private String command = NO_VALUE;
    private String teamDomain = NO_VALUE;
    private String teamId = NO_VALUE;
    private String text = NO_VALUE;
    private String token = NO_VALUE;
    private String userId = NO_VALUE;
    private String username = NO_VALUE;

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
        log.debug(String.format("%s Parsing body", TAG));
        String[] bodyParams = body.split("&");
        if (bodyParams.length == 0) {
            log.debug(String.format("%s Body has 0 params: nothing to do", TAG));
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
                log.debug(String.format("%s String '%s' doesn't match any pattern. Skipping...", TAG, this.param));
            }
        }
    }

    private String decodeText(String encodedString) {
        return URLDecoder.decode(encodedString);
    }

    private void parseText() {
        log.debug(String.format("%s Parsing text", TAG));
        if (StringUtils.isBlank(this.text) || this.text.equals(NO_VALUE)) {
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

        private String value;

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

        private String value;

        ResponseType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class Constants {
        private Constants() {
        }

        public static final String AT = "@";
        public static final String NO_VALUE = "_";
        public static final String BOT_ICON = "https://yals.eu/favicon.ico";
        public static final String BOT_NAME = "YalsBot";
        public static final String SUPPORT_URL = "https://github.com/yadevee/yals/issues";
    }

    public static class Emoji {
        private Emoji() {
        }

        public static final String WARNING = ":warning:";
        public static final String INFO = ":information_source:";
    }
}
