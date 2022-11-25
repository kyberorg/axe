package pm.axe.test.unit;

import pm.axe.constants.App;
import pm.axe.mm.Mattermost;
import pm.axe.test.utils.mock.MattermostMock;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Mattermost}.
 *
 * @since 2.3
 */
public class MattermostTests extends UnitTest {
    private static final int CHANNEL_ID_LENGTH = 6;
    private static final int TEAM_ID_LENGTH = 6;
    private static final int USER_ID_LENGTH = 6;
    private static final int TOKEN_LENGTH = 15;

    /**
     * Valid {@link Mattermost} object from valid request.
     */
    @Test
    public void shouldBeValidObjectFromValidRequest() {
        String host = "axe.pm";
        String text = "https%3A%2F%2F" + host;

        String channelId = RandomStringUtils.randomAlphanumeric(CHANNEL_ID_LENGTH);
        String channelName = "channelName";
        String command = "axe";
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

    /**
     * {@link IllegalStateException} expected when empty body.
     */
    @Test
    public void shouldBeExceptionWhenRequestHasBlankBody() {
        assertThrows(IllegalStateException.class, () -> Mattermost.createFromResponseBody(""));
    }

    /**
     * {@link IllegalStateException} expected when null body.
     */
    @Test
    public void shouldBeExceptionWhenRequestHasNullBody() {
        assertThrows(IllegalStateException.class, () -> Mattermost.createFromResponseBody(null));
    }

    /**
     * When empty body - exception expected.
     */
    @Test
    public void shouldBeExceptionWhenRequestHasEmptyBody() {
        assertThrows(IllegalStateException.class, () -> Mattermost.createFromResponseBody(" "));
    }

    /**
     * {@link NoSuchElementException} expected when request has no text.
     */
    @Test
    public void shouldBeExceptionWhenRequestHasNoText() {
        String channelId = RandomStringUtils.randomAlphanumeric(CHANNEL_ID_LENGTH);
        String channelName = "channelName";
        String command = "axe";
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

        assertThrows(NoSuchElementException.class, () -> Mattermost.createFromResponseBody(matterMock.toString()));

    }

    /**
     * Valid {@link Mattermost} object with missing fields expected if those fields are not present in body.
     */
    @Test
    public void shouldBeValidObjectWithMissingFieldsWhenAreNotPresentIsBody() {
        String host = "axe.pm";
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

    /**
     * {@link IllegalArgumentException} expected when text is not URL.
     */
    @Test
    public void shouldBeExceptionWhenTextIsNotUrl() {
        String text = "notAnUrl";
        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        String userId = RandomStringUtils.randomAlphanumeric(USER_ID_LENGTH);
        String uzer = "uzer";

        MattermostMock matterMock = MattermostMock.create()
                .withText(text)
                .withToken(token)
                .withUserId(userId).withUsername(uzer);

        assertThrows(IllegalArgumentException.class, () -> Mattermost.createFromResponseBody(matterMock.toString()));
    }

    /**
     * URL is text should be correctly decoded.
     */
    @Test
    public void urlInTextShouldBeDecodedIfValidUrl() {
        String text = "https%3A%2F%2Faxe.pm";
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
