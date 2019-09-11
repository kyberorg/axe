package ee.yals.test.it;

import ee.yals.Endpoint;
import ee.yals.json.EmptyJson;
import ee.yals.json.StoreRequestJson;
import ee.yals.json.StoreResponseJson;
import ee.yals.utils.AppUtils;
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

import static ee.yals.test.utils.TestUtils.assertResultIsErrorJson;
import static ee.yals.test.utils.TestUtils.assertResultIsJson;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit testing for store API
 *
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class StoreControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void onRequestWithoutBodyStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        mockMvc.perform(post(Endpoint.STORE_API))
                .andExpect(status().is(400));
    }

    @Test
    public void onRequestWithEmptyBodyStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        mockMvc.perform(post(Endpoint.STORE_API).content(""))
                .andExpect(status().is(400));
    }

    @Test
    public void onRequestWithNonJsonBodyStatusIs421() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(post(Endpoint.STORE_API).content("not a JSON"))
                .andExpect(status().is(421))
                .andReturn();
        assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithJSONWithoutLinkParamStatusIs421() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(post(Endpoint.STORE_API).content(EmptyJson.create().toString()))
                .andExpect(status().is(421))
                .andReturn();
        assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithEmptyLinkStatusIs421() throws Exception {
        String longLink = "";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(post(Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(421))
                .andReturn();
        assertResultIsErrorJson(result);
    }

    @Test
    public void onRequestWithNotALinkStatusIs421() throws Exception {
        String longLink = "notALink";
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        mockMvc.perform(post(Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(421));
    }

    @Test
    public void onRequestWithCorrectLinkStatusIs201() throws Exception {
        String longLink = "http://virtadev.net"; //That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(post(Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(201))
                .andReturn();

        assertResultIsJson(result);
    }

    @Test
    public void onRequestWithCorrectLinkReturnsJsonWithIdent() throws Exception {
        String longLink = "http://virtadev.net"; //That very long, really
        String correctJson = StoreRequestJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(post(Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(201))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson;
        try {
            replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        } catch (Exception e) {
            fail("Could not parse reply JSON");
            return;
        }

        assertNotNull(replyJson);
        assertNotNull(replyJson.getIdent());
    }

}
