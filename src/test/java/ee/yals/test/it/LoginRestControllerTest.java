package ee.yals.test.it;

import ee.yals.Endpoint;
import ee.yals.controllers.rest.LoginRestController;
import ee.yals.json.ErrorJson;
import ee.yals.json.LoginRequestJson;
import ee.yals.json.LoginResponseJson;
import ee.yals.test.YalsTest;
import ee.yals.utils.AppUtils;
import org.apache.commons.lang3.StringUtils;
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

import static ee.yals.test.utils.TestUtils.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test set for {@link LoginRestController}
 *
 * @since 3.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-app.properties")
public class LoginRestControllerTest extends YalsTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void withCorrectUserAndPassLoginSendsBackToken() throws Exception {
        assertMockMvcAvailable(this.mockMvc);

        String username = "demo";
        String password = "demo";

        String body = LoginRequestJson.createWithUsername(username).andPassword(password).toString();

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body))
                .andExpect(status().is(200))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());

        LoginResponseJson replyJson = AppUtils.GSON.fromJson(responseBody, LoginResponseJson.class);
        assertNotNull("Reply is suddenly NULL", replyJson);
        assertTrue("Token is absent", StringUtils.isNotBlank(replyJson.getToken()));
    }

    @Test
    public void whenNotAJsonShouldBeErrorJsonWith421() throws Exception {
        String body = "Not a JSON";

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body))
                .andExpect(status().is(421))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());
        assertResultIsErrorJson(result);

        ErrorJson errorJson = AppUtils.GSON.fromJson(responseBody, ErrorJson.class);
        assertErrorMessageContainsText(errorJson, "parse");
        assertErrorMessageContainsText(errorJson, "JSON");
    }

    @Test
    public void whenNotAllFieldsInJsonArePresentShouldGive400() throws Exception {
        assertMockMvcAvailable(mockMvc);

        String username = "demo";
        String body = LoginRequestJson.createWithUsername(username).andPassword(null).toString();

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body))
                .andExpect(status().is(400))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());
        assertResultIsErrorJson(result);
    }

    @Test
    public void whenUserNotFoundShouldBeErrorJsonWith401() throws Exception {
        assertMockMvcAvailable(mockMvc);
        String username = "uzerABC";
        String password = "thePassword";
        String body = LoginRequestJson.createWithUsername(username).andPassword(password).toString();

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body))
                .andExpect(status().is(401))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());
        assertResultIsErrorJson(result);
    }

    @Test
    public void whenUserNotFoundErrorMessageShouldContainBothUsernameAndPassword() throws Exception {
        assertMockMvcAvailable(mockMvc);
        String username = "uzerABC";
        String password = "thePassword";
        String body = LoginRequestJson.createWithUsername(username).andPassword(password).toString();

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body)).andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());
        assertResultIsErrorJson(result);
        ErrorJson errorJson = AppUtils.GSON.fromJson(responseBody, ErrorJson.class);
        assertErrorMessageContainsText(errorJson, "user");
        assertErrorMessageContainsText(errorJson, "password");
    }

    @Test
    public void whenPasswordIsWrongShouldBeErrorJsonWith401() throws Exception {
        assertMockMvcAvailable(mockMvc);

        String username = "demo";
        String password = "thePassword";
        String body = LoginRequestJson.createWithUsername(username).andPassword(password).toString();

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body))
                .andExpect(status().is(401))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());
        assertResultIsErrorJson(result);
    }

    @Test
    public void whenPasswordIsWrongErrorMessageShouldContainBothUsernameAndPassword() throws Exception {
        assertMockMvcAvailable(mockMvc);

        String username = "demo";
        String password = "thePassword";
        String body = LoginRequestJson.createWithUsername(username).andPassword(password).toString();

        MvcResult result = mockMvc.perform(post(Endpoint.LOGIN_API).content(body)).andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull("Login endpoint responded with empty body", responseBody);
        assertFalse("Login endpoint responded with empty body", responseBody.trim().isEmpty());
        assertResultIsErrorJson(result);
        ErrorJson errorJson = AppUtils.GSON.fromJson(responseBody, ErrorJson.class);
        assertErrorMessageContainsText(errorJson, "user");
        assertErrorMessageContainsText(errorJson, "password");
    }
}