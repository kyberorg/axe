package eu.yals.test.it;

import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.controllers.rest.MattermostRestController;
import eu.yals.json.MattermostResponseJson;
import eu.yals.test.utils.mock.MattermostMock;
import eu.yals.utils.AppUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static eu.yals.test.TestUtils.assertContentType;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Usage tests for {@link MattermostRestController}
 *
 * @since 2.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class MattermostRestControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void toNormalPayloadShouldReplyWith200AndCorrectJsonAndWithContentType() throws Exception {

        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("https%3A%2F%2Fyals.eu")
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        String matterRequest = matterMock.toString();

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterRequest)
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void toPayloadWithTrailingSpaceShouldReplyCorrectJson() throws Exception {

        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("https%3A%2F%2Fyals.eu+") //Space encodes as +
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        String matterRequest = matterMock.toString();

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterRequest)
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);
    }


    @Test
    public void toPayloadWithUsernameShouldReplyWithCorrectJsonAndTextContainsThisUser() throws Exception {

        String uzer = "uzer";

        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("https%3A%2F%2Fyals.eu")
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername(uzer);

        String matterRequest = matterMock.toString();

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterRequest)
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);
        assertTrue("Mattermost test should have username in message", mmText.contains(App.AT + uzer));
    }


    @Test
    public void forReplyWithoutBodyShouldReplyWith400() throws Exception {
        mockMvc.perform(
                post(Endpoint.MM_API)
                        .header(Header.TEST, true))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    public void forReplyWithStrangeBodyShouldReplyWithUsage() throws Exception {
        String strangeBody = "a=haba$b=more";
        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(strangeBody)
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);

        assertUsage(mmText);
    }

    @Test
    public void forReplyWhereTextIsNotLinkShouldReplyWithCorrectMMJsonAndErrorMessageWithUsage() throws Exception {
        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("ThisIsStringWithoutUrl")
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterMock.toString())
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);

        assertUsage(mmText);
    }

    @Test
    public void whenArgIsOnlySingleSpaceShouldShowUsage() throws Exception {
        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("+")
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterMock.toString())
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    @Test
    public void whenArgContainsOnlySpacesShouldShowUsage() throws Exception {
        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("+++")
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterMock.toString())
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    @Test
    public void whenTextContainTwoArgsAndFirstIsNotLinkShouldShowUsage() throws Exception {
        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("First+Second")
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterMock.toString())
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue(isResultMattermostReplyJson(result));
        assertContentType(MimeType.APPLICATION_JSON, result);

        String mmText = getMMText(result);
        assertUsage(mmText);
    }

    @Test
    public void whenTextIsURLAndTextShouldReturnShortLinkAndDescription() throws Exception {
        String description = "TestDescription";
        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("https%3A%2F%2Fyals.eu+" + description)
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        String matterRequest = matterMock.toString();

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterRequest)
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue("Reply should valid " + MattermostResponseJson.class.getSimpleName() + " object",
                isResultMattermostReplyJson(result));
        String mmText = getMMText(result);
        assertTrue("Text must contain description, if it is present", mmText.contains(description));
    }

    @Test
    public void whenTextHasMultiWordDescriptionShouldReturnShortLinkAndMultiWordDescription() throws Exception {
        String description = "Multi Test Description";
        String encodedDescription = URLEncoder.encode(description, "UTF-8");
        MattermostMock matterMock = MattermostMock.create()
                .withChannelId(RandomStringUtils.randomAlphanumeric(6)).withChannelName("channelName")
                .withCommand("yals")
                .withTeamDomain("myTeam").withTeamId(RandomStringUtils.randomAlphanumeric(6))
                .withText("https%3A%2F%2Fyals.eu+" + encodedDescription)
                .withToken(RandomStringUtils.randomAlphanumeric(15))
                .withUserId(RandomStringUtils.randomAlphanumeric(6)).withUsername("uzer");

        String matterRequest = matterMock.toString();

        MvcResult result = mockMvc.perform(
                post(Endpoint.MM_API)
                        .content(matterRequest)
                        .header(Header.TEST, true))
                .andExpect(status().is(200))
                .andReturn();
        assertTrue("Reply should valid " + MattermostResponseJson.class.getSimpleName() + " object",
                isResultMattermostReplyJson(result));
        String mmText = getMMText(result);
        assertTrue("Text must contain description, if it is present", mmText.contains(description));
    }

    private boolean isResultMattermostReplyJson(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        try {
            MattermostResponseJson mmJson = AppUtils.GSON.fromJson(body, MattermostResponseJson.class);
            return mmJson != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String getMMText(MvcResult result) throws UnsupportedEncodingException {
        String body = result.getResponse().getContentAsString();
        MattermostResponseJson mmJson = AppUtils.GSON.fromJson(body, MattermostResponseJson.class);
        return mmJson.getText();
    }

    private void assertMMError(String mmText) {
        assertTrue("Text must contain warning emoji", mmText.contains(App.Emoji.WARNING));
    }

    private void assertUsage(String mmText) {
        assertTrue("Text must contain info emoji", mmText.contains(App.Emoji.INFO));
        assertTrue("Text must contain word 'Usage'", mmText.contains("Usage"));
    }

}
