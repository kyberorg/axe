package eu.yals.test.utils.mock;

import eu.yals.constants.App;
import eu.yals.mm.Mattermost.Marker;

import java.util.ArrayList;

/**
 * Build request string with comes from any Mattermost server.
 *
 * @since 2.3
 */
public final class MattermostMock {

    private final ArrayList<String> collector;
    private final StringBuilder sb = new StringBuilder();

    private MattermostMock() {
        collector = new ArrayList<>();
    }

    /**
     * Creates {@link MattermostMock}.
     *
     * @return empty object
     */
    public static MattermostMock create() {
        return new MattermostMock();
    }

    /**
     * Creates {@link MattermostMock} with given channel id.
     *
     * @param channelId string with channel id
     * @return {@link MattermostMock}
     */
    public MattermostMock withChannelId(final String channelId) {
        collector.add(createParam(Marker.CHANNEL_ID, channelId));
        return this;
    }

    /**
     * Adds chanel name to mock.
     *
     * @param channelName string with MM channel name
     * @return {@link MattermostMock}
     */
    public MattermostMock withChannelName(final String channelName) {
        collector.add(createParam(Marker.CHANNEL_NAME, channelName));
        return this;
    }

    /**
     * Adds Mattermost command.
     *
     * @param command string with command
     * @return {@link MattermostMock}
     */
    public MattermostMock withCommand(final String command) {
        collector.add(createParam(Marker.COMMAND, command));
        return this;
    }

    /**
     * Adds team name (aka domain).
     *
     * @param teamDomain string team domain
     * @return {@link MattermostMock}
     */
    public MattermostMock withTeamDomain(final String teamDomain) {
        collector.add(createParam(Marker.TEAM_DOMAIN, teamDomain));
        return this;
    }

    /**
     * ID of team.
     *
     * @param teamId string with mm team id
     * @return {@link MattermostMock}
     */
    public MattermostMock withTeamId(final String teamId) {
        collector.add(createParam(Marker.TEAM_ID, teamId));
        return this;
    }

    /**
     * Adds message.
     *
     * @param text string with message
     * @return {@link MattermostMock}
     */
    public MattermostMock withText(final String text) {
        collector.add(createParam(Marker.TEXT, text));
        return this;
    }

    /**
     * Adds mattermost token.
     *
     * @param token string with token
     * @return {@link MattermostMock}
     */
    public MattermostMock withToken(final String token) {
        collector.add(createParam(Marker.TOKEN, token));
        return this;
    }

    /**
     * Adds sender's ID.
     *
     * @param userId string with sender's id
     * @return {@link MattermostMock}
     */
    public MattermostMock withUserId(final String userId) {
        collector.add(createParam(Marker.USER_ID, userId));
        return this;
    }

    /**
     * Adds sender username.
     *
     * @param username string with username of sender
     * @return {@link MattermostMock}
     */
    public MattermostMock withUsername(final String username) {
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

    private String createParam(final Marker marker, final String value) {
        sb.setLength(0);
        sb.append(marker).append(App.EQUAL).append(value);
        return sb.toString();
    }
}
