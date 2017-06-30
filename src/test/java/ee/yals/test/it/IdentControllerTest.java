package ee.yals.test.it;

import ee.yals.Endpoint;
import ee.yals.json.StoreJson;
import ee.yals.json.StoreReplyJson;
import ee.yals.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static ee.yals.test.utils.TestUtils.assertResultIsJson;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class description
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
public class IdentControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void onRequestWithoutIdentStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(get(Endpoint.LINK_API))
                .andExpect(status().is(400))
                .andReturn();

        assertResultIsJson(result);
    }

    @Test
    public void onRequestWithSpaceIdentStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(get(Endpoint.LINK_API + " "))
                .andExpect(status().is(400))
                .andReturn();
        assertResultIsJson(result);
    }

    @Test
    public void onRequestWithSpecialCharIdentStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(get(Endpoint.LINK_API + "%#"))
                .andExpect(status().is(400))
                .andReturn();

       assertResultIsJson(result);
    }

    @Test
    public void onRequestWithNotExistingIdentStatusIs404() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(get(Endpoint.LINK_API + "notStoredIdent"))
                .andExpect(status().is(404))
        .andReturn();
        assertResultIsJson(result);
    }

    @Test
    public void onRequestWithExistingIdentStatusIs200() throws Exception {
        assertNotNull(this.mockMvc);
        String longLink = "http://virtadev.net"; //That very long, really
        String ident = store(longLink);

        MvcResult result = mockMvc.perform(get(Endpoint.LINK_API + ident))
                .andExpect(status().is(200))
                .andReturn();
        assertResultIsJson(result);
    }

    private String store(String longLink) throws Exception {

        String requestJson = StoreJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(post(Endpoint.STORE_API).content(requestJson))
                .andExpect(status().is(201))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreReplyJson replyJson;
        replyJson = TestUtils.gson().fromJson(responseBody, StoreReplyJson.class);
        return replyJson.getIdent();
    }

}
