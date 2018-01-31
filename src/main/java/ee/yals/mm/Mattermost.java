package ee.yals.mm;

import ee.yals.constants.App;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import static ee.yals.mm.Mattermost.Constants.NO_VALUE;

/**
 * Mattermost related code
 *
 * @since 2.3
 */
public class Mattermost {
    private static final Logger Log = Logger.getLogger(Mattermost.class);


    private String channelId = NO_VALUE;
    private String channelName = NO_VALUE;
    private String command = NO_VALUE;
    private String teamDomain = NO_VALUE;
    private String teamId = NO_VALUE;
    private String text = NO_VALUE;
    private String token = NO_VALUE;
    private String userId = NO_VALUE;
    private String username = NO_VALUE;

    private String param;

    private Mattermost() {
    }

    public static Mattermost createFromResponseBody(String body) {
        if (StringUtils.isBlank(body)) {
            throw new IllegalArgumentException("Body is missing");
        }
        Mattermost mm = new Mattermost();
        mm.parseBody(body);
        if (StringUtils.isBlank(mm.text) || mm.text.equals(NO_VALUE)) {
            throw new IllegalStateException("Param 'text' is missing");
        } else {
            mm.text = mm.decodeUrlInText();
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

    private void parseBody(String body) {
        String[] bodyParams = body.split("&");
        if (bodyParams.length == 0) {
            return; //nothing to do
        }
        for (String bodyParam : bodyParams) {
            this.param = bodyParam;
            if (isParamStartWith(Marker.CHANNEL_ID)) {
                channelId = removeMarker(Marker.CHANNEL_ID);
            } else if (isParamStartWith(Marker.CHANNEL_NAME)) {
                channelName = removeMarker(Marker.CHANNEL_NAME);
            } else if (isParamStartWith(Marker.COMMAND)) {
                command = removeMarker(Marker.COMMAND);
                command = decodeText(command);
            } else if (isParamStartWith(Marker.TEAM_DOMAIN)) {
                teamDomain = removeMarker(Marker.TEAM_DOMAIN);
            } else if (isParamStartWith(Marker.TEAM_ID)) {
                teamId = removeMarker(Marker.TEAM_ID);
            } else if (isParamStartWith(Marker.TEXT)) {
                text = removeMarker(Marker.TEXT);
            } else if (isParamStartWith(Marker.TOKEN)) {
                token = removeMarker(Marker.TOKEN);
            } else if (isParamStartWith(Marker.USER_ID)) {
                userId = removeMarker(Marker.USER_ID);
            } else if (isParamStartWith(Marker.USER_NAME)) {
                username = removeMarker(Marker.USER_NAME);
            } else {
                Log.warn("String '" + this.param + "' doesn't match any pattern. Skipping...");
            }

        }
    }

    private String decodeUrlInText() {
        try {
            URI uri = new URI(this.text);
            return uri.getPath();
        } catch (URISyntaxException e) {
            return this.text;
        }
    }

    private String decodeText(String encodedString) {
        return URLDecoder.decode(encodedString);
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
        public static final String BOT_ICON = "https://yals.ee/favicon.ico";
        public static final String BOT_NAME = "YalsBot";
        public static final String SUPPORT_URL = "https://github.com/yadevee/yals/issues";
    }

    public static class Emoji {
        private Emoji() {
        }

        public static final String WARNING = ":warning:";
    }
}
