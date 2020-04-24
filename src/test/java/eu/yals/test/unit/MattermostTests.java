package eu.yals.test.unit;

import eu.yals.constants.App;
import eu.yals.mm.Mattermost;
import eu.yals.test.utils.mock.MattermostMock;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Tests for {@link Mattermost}
 *
 * @since 2.3
 */
public class MattermostTests {
    private static final int CHANNEL_ID_LENGTH = 6;
    private static final int TEAM_ID_LENGTH = 6;
    private static final int USER_ID_LENGTH = 6;
    private static final int TOKEN_LENGTH = 15;

    @Test
    public void shouldBeValidObjectFromValidRequest() {
        String host = "yals.eu";
        String text = "https%3A%2F%2F" + host;

        String channelId = RandomStringUtils.randomAlphanumeric(CHANNEL_ID_LENGTH);
        String channelName = "channelName";
        String command = "yals";
        String teamDomain = "myTeam";
        String teamId = RandomStringUtils.randomAlphanumeric(TEAM_ID_LENGTH);
        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        String userId = RandomStringUtils.randomAlphanumeric(USER_ID_LENGTH);
        String uzer = "uzer";

        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(channelId).withChannelName(channelName)
                .withCommand(command)
                .withTeamDomain(teamDomain).withTeamId(teamId)
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost mm = Mattermost.createFromResponseBody(matterMock.toString());

        assertEquals(channelId, mm.getChannelId());
        assertEquals(channelName, mm.getChannelName());
        assertEquals(command, mm.getCommand());
        assertEquals(teamDomain, mm.getTeamDomain());
        assertEquals(teamId, mm.getTeamId());
        assertTrue(mm.getText().contains(host));
        assertEquals(token, mm.getToken());
        assertEquals(userId, mm.getUserId());
        assertEquals(uzer, mm.getUsername());

    }

    @Test(expected = IllegalStateException.class)
    public void shouldBeExceptionWhenRequestHasBlankBody() {
        Mattermost.createFromResponseBody("");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBeExceptionWhenRequestHasNullBody() {
        Mattermost.createFromResponseBody(null);
    }

    /**
     * When empty body - exception expected.
     */
    @Test(expected = IllegalStateException.class)
    public void shouldBeExceptionWhenRequestHasEmptyBody() {
        Mattermost.createFromResponseBody(" ");
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldBeExceptionWhenRequestHasNoText() {
        String channelId = RandomStringUtils.randomAlphanumeric(CHANNEL_ID_LENGTH);
        String channelName = "channelName";
        String command = "yals";
        String teamDomain = "myTeam";
        String teamId = RandomStringUtils.randomAlphanumeric(TEAM_ID_LENGTH);
        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        String userId = RandomStringUtils.randomAlphanumeric(USER_ID_LENGTH);
        String uzer = "uzer";

        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(channelId).withChannelName(channelName)
                .withCommand(command)
                .withTeamDomain(teamDomain).withTeamId(teamId)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost.createFromResponseBody(matterMock.toString());
    }

    @Test
    public void shouldBeValidObjectWithMissingFieldsWhenAreNotPresentIsBody() {
        String host = "yals.eu";
        String text = "https%3A%2F%2F" + host;

        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        String userId = RandomStringUtils.randomAlphanumeric(USER_ID_LENGTH);
        String uzer = "uzer";


        MattermostMock matterMock = MattermostMock.create()
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost mm = Mattermost.createFromResponseBody(matterMock.toString());

        assertEquals(App.NO_VALUE, mm.getChannelId());
        assertEquals(App.NO_VALUE, mm.getChannelName());
        assertEquals(App.NO_VALUE, mm.getCommand());
        assertEquals(App.NO_VALUE, mm.getTeamDomain());
        assertEquals(App.NO_VALUE, mm.getTeamId());
        assertTrue(mm.getText().contains(host));
        assertEquals(token, mm.getToken());
        assertEquals(userId, mm.getUserId());
        assertEquals(uzer, mm.getUsername());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeExceptionWhenTextIsNotUrl() {
        String text = "notAnUrl";
        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        String userId = RandomStringUtils.randomAlphanumeric(USER_ID_LENGTH);
        String uzer = "uzer";


        MattermostMock matterMock = MattermostMock.create()
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost.createFromResponseBody(matterMock.toString());
    }

    @Test
    public void urlInTextShouldBeDecodedIfValidUrl() {
        String text = "https%3A%2F%2Fyals.eu";
        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        String userId = RandomStringUtils.randomAlphanumeric(USER_ID_LENGTH);
        String uzer = "uzer";


        MattermostMock matterMock = MattermostMock.create()
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost mm = Mattermost.createFromResponseBody(matterMock.toString());
        assertNotEquals(text, mm.getText());
        assertTrue(mm.getText().contains("://"));
    }

}
