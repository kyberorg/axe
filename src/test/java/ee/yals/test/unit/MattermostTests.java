package ee.yals.test.unit;

import ee.yals.mm.Mattermost;
import ee.yals.test.utils.mock.MattermostMock;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.NoSuchElementException;

import static ee.yals.mm.Mattermost.Constants.NO_VALUE;
import static org.junit.Assert.*;

/**
 * Tests for {@link ee.yals.mm.Mattermost}
 *
 * @since 2.3
 */
public class MattermostTests {

    @Test
    public void shouldBeValidObjectFromValidRequest() {
        String host = "yals.ee";
        String text = "https%3A%2F%2F" + host;

        String channelId = RandomStringUtils.randomAlphanumeric(6);
        String channelName = "channelName";
        String command = "yals";
        String teamDomain = "myTeam";
        String teamId = RandomStringUtils.randomAlphanumeric(6);
        String token = RandomStringUtils.randomAlphanumeric(15);
        String userId = RandomStringUtils.randomAlphanumeric(6);
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

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeExceptionWhenRequestHasBlankBody() {
        Mattermost.createFromResponseBody("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeExceptionWhenRequestHasNullBody() {
        Mattermost.createFromResponseBody(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldBeExceptionWhenRequestHasEmptyBody() {
        Mattermost.createFromResponseBody(" ");
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldBeExceptionWhenRequestHasNoText() {
        String channelId = RandomStringUtils.randomAlphanumeric(6);
        String channelName = "channelName";
        String command = "yals";
        String teamDomain = "myTeam";
        String teamId = RandomStringUtils.randomAlphanumeric(6);
        String token = RandomStringUtils.randomAlphanumeric(15);
        String userId = RandomStringUtils.randomAlphanumeric(6);
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
        String host = "yals.ee";
        String text = "https%3A%2F%2F" + host;

        String token = RandomStringUtils.randomAlphanumeric(15);
        String userId = RandomStringUtils.randomAlphanumeric(6);
        String uzer = "uzer";


        MattermostMock matterMock = MattermostMock.create()
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost mm = Mattermost.createFromResponseBody(matterMock.toString());

        assertEquals(NO_VALUE, mm.getChannelId());
        assertEquals(NO_VALUE, mm.getChannelName());
        assertEquals(NO_VALUE, mm.getCommand());
        assertEquals(NO_VALUE, mm.getTeamDomain());
        assertEquals(NO_VALUE, mm.getTeamId());
        assertTrue(mm.getText().contains(host));
        assertEquals(token, mm.getToken());
        assertEquals(userId, mm.getUserId());
        assertEquals(uzer, mm.getUsername());
    }

    @Test
    public void shouldBeValidObjectWhenTextIsNotUrl() {
        String text = "notAnUrl";
        String token = RandomStringUtils.randomAlphanumeric(15);
        String userId = RandomStringUtils.randomAlphanumeric(6);
        String uzer = "uzer";


        MattermostMock matterMock = MattermostMock.create()
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        Mattermost mm = Mattermost.createFromResponseBody(matterMock.toString());

        assertEquals(text, mm.getText());
        assertEquals(token, mm.getToken());
    }

    @Test
    public void urlInTextShouldBeDecodedIfValidUrl() {
        String text = "https%3A%2F%2Fyals.ee";
        String token = RandomStringUtils.randomAlphanumeric(15);
        String userId = RandomStringUtils.randomAlphanumeric(6);
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
