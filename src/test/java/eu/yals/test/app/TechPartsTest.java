package eu.yals.test.app;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.test.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing Tech Parts and other non-standard locations
 *
 * @since 2.5.1
 */
public class TechPartsTest extends UnirestTest {

    private static final String ALWAYS_NOT_FOUND_LOCATION = "/void/notFound";
    private static final String ALWAYS_NOT_FOUND_API_LOCATION = "/api/void/notFound";

    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenNothingFound() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void byDefaultAppReturnHtmlWhenNothingFound() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.TEXT_HTML, response);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenNothingFound() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenNothingFound() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenNothingFound() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML)
                .asString();

        assertEquals(406, response.getStatus());
    }

    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenFailed() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void byDefaultAppReturnHtmlWhenFailed() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_ENDPOINT)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.TEXT_HTML, response);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenFailed() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenFailed() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenFailed() throws Exception {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML)
                .asString();

        assertEquals(406, response.getStatus());
    }
}
