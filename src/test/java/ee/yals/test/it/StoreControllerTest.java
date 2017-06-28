package ee.yals.test.it;

import ee.yals.json.EmptyJson;
import ee.yals.json.StoreJson;
import ee.yals.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit testing for store API
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
public class StoreControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockHttpServletRequest request;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void onRequestWithoutBodyStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API))
                .andExpect(status().is(400));
    }

    @Test
    public void onRequestWithEmptyBodyStatusIs400() throws Exception {
        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API).content(""))
                .andExpect(status().is(400));
    }

    @Test
    public void onRequestWithNonJsonBodyStatusIs421() throws Exception {
        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API).content("not a JSON"))
                .andExpect(status().is(421));
    }

    @Test
    public void onRequestWithJSONWithoutLinkParamStatusIs421() throws Exception {

        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API).content(EmptyJson.create().toString()))
                .andExpect(status().is(421));
    }

    @Test
    public void onRequestWithEmptyLinkStatusIs400() throws Exception {
        String longLink = "";
        String correctJson = StoreJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(400));
    }

    @Test
    public void onRequestWithNotALinkStatusIs400() throws Exception {
        String longLink = "";
        String correctJson = StoreJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(400));
    }

    @Test
    public void onRequestWithCorrectLinkStatusIs201() throws Exception {
        String longLink = "http://virtadev.net"; //That very long, really
        String correctJson = StoreJson.create().withLink(longLink).toString();

        assertNotNull(this.mockMvc);
        mockMvc.perform(post(TestUtils.Endpoint.STORE_API).content(correctJson))
                .andExpect(status().is(201));
    }

}
