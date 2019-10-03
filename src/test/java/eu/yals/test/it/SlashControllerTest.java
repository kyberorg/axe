package eu.yals.test.it;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.controllers.SlashController;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.utils.AppUtils;
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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests for {@link SlashController}
 *
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class SlashControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void storeURLAndCheckIfRedirectToSameURL() throws Exception {
        String url = "https://eesti.ee";
        String ident = store(url);
        assertNotNull(ident);

        MvcResult result = mockMvc.perform(
                get(Endpoint.SLASH_BASE + ident)
                        .header(Header.TEST, true))
                .andExpect(status().is(302))
                .andReturn();

        String locationHeader = result.getResponse().getHeader("Location");
        assertNotNull(locationHeader);

        assertEquals(url, locationHeader);
    }

    @Test
    public void requestWithIdentThatNotStoredGivesStatus404() throws Exception {
        String ident = "habaHaba";

        mockMvc.perform(
                get(Endpoint.SLASH_BASE + ident)
                        .header(Header.TEST, true))
                .andExpect(status().is(404));

    }

    private String store(String urlToStore) throws Exception {
        String request = StoreRequestJson.create().withLink(urlToStore).toString();
        MvcResult storeResult = mockMvc.perform(post(Endpoint.STORE_API)
                .contentType(MimeType.APPLICATION_JSON).content(request))
                .andExpect(status().is(201))
                .andReturn();

        String responseBody = storeResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        assertFalse(responseBody.trim().isEmpty());

        StoreResponseJson replyJson = AppUtils.GSON.fromJson(responseBody, StoreResponseJson.class);
        assertNotNull(replyJson);
        return replyJson.getIdent();
    }

    public void homePageDisplaysCorrectly() throws Exception {
        assertNotNull(this.mockMvc);
        mockMvc.perform(get(Endpoint.SLASH_BASE))
                .andExpect(view().name("index"));
    }

}
