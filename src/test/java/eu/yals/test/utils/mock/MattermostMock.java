package eu.yals.test.utils.mock;

import eu.yals.constants.App;
import eu.yals.mm.Mattermost.Marker;

import java.util.ArrayList;

/**
 * Build request string with comes from any Mattermost server
 *
 * @since 2.3
 */
public class MattermostMock {

    private final ArrayList<String> collector;
    private final StringBuilder sb = new StringBuilder();

    private MattermostMock() {
        collector = new ArrayList<>(8);
    }

    public static MattermostMock create() {
        return new MattermostMock();
    }

    public MattermostMock withChannelId(String channelId) {
        collector.add(createParam(Marker.CHANNEL_ID, channelId));
        return this;
    }

    public MattermostMock withChannelName(String channelName) {
        collector.add(createParam(Marker.CHANNEL_NAME, channelName));
        return this;
    }

    public MattermostMock withCommand(String command) {
        collector.add(createParam(Marker.COMMAND, command));
        return this;
    }

    public MattermostMock withTeamDomain(String teamDomain) {
        collector.add(createParam(Marker.TEAM_DOMAIN, teamDomain));
        return this;
    }

    public MattermostMock withTeamId(String teamId) {
        collector.add(createParam(Marker.TEAM_ID, teamId));
        return this;
    }

    public MattermostMock withText(String text) {
        collector.add(createParam(Marker.TEXT, text));
        return this;
    }

    public MattermostMock withToken(String token) {
        collector.add(createParam(Marker.TOKEN, token));
        return this;
    }

    public MattermostMock withUserId(String userId) {
        collector.add(createParam(Marker.USER_ID, userId));
        return this;
    }

    public MattermostMock withUsername(String username) {
        collector.add(createParam(Marker.USER_NAME, username));
        return this;
    }

    @Override
    public String toString() {
        sb.setLength(0);
        for (String str : collector) {
            sb.append(str).append(App.AND);
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    private String createParam(Marker marker, String value) {
        sb.setLength(0);
        sb.append(marker).append(App.EQUAL).append(value);
        return sb.toString();
    }

}
