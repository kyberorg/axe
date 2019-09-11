package ee.yals.test.it;

import ee.yals.Endpoint;
import ee.yals.constants.Header;
import ee.yals.constants.MimeType;
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

import static ee.yals.test.utils.TestUtils.assertContentNotEmpty;
import static ee.yals.test.utils.TestUtils.assertContentType;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Retrieving application static resources
 *
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:test-app.xml"})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class StaticControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testCssIsPresentAndCss() throws Exception {
        assertNotNull(this.mockMvc);
        MvcResult result = mockMvc.perform(get(Endpoint.TEST_CSS)
                .header(Header.TEST, ""))
                .andExpect(status().is(200))
                .andReturn();

        assertContentNotEmpty(Endpoint.TEST_CSS + " is empty", result);
        assertContentType(MimeType.TEXT_CSS, result);
    }
}
