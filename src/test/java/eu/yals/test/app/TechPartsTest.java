package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.test.TestUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import static org.junit.Assert.assertEquals;

/**
 * Testing Tech Parts and other non-standard locations
 *
 * @since 2.5.1
 */
@Slf4j
public class TechPartsTest extends UnirestTest {

    private static final String ALWAYS_NOT_FOUND_LOCATION = "/void/notFound";
    private static final String ALWAYS_NOT_FOUND_API_LOCATION = "/api/void/notFound";

    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenNothingFound() {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void byDefaultAppReturnHtmlWhenNothingFound() {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.TEXT_HTML, response);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenNothingFound() {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenNothingFound() {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .asString();

        assertEquals(404, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenNothingFound() {
        HttpResponse<String> response = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML)
                .asString();

        assertEquals(406, response.getStatus());
    }

    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenFailed() {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void byDefaultAppReturnHtmlWhenFailed() {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_ENDPOINT)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.TEXT_HTML, response);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenFailed() {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenFailed() {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .asString();

        assertEquals(500, response.getStatus());

        TestUtils.assertResponseBodyNotEmpty(response);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, response);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenFailed() {
        HttpResponse<String> response = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML)
                .asString();

        assertEquals(406, response.getStatus());
    }

    @Test
    public void robotsTxtIsPresentAndText() {
        String robotsTxt = String.format("%s%s", TestUtils.getTestUrl(), Endpoint.ROBOTS_TXT);
        HttpResponse<String> response = TestUtils.unirestGet(robotsTxt);
        log.debug("Response: {}", response);
        if (response == null) return;

        Assert.assertEquals(200, response.getStatus());

        String body = response.getBody();
        Assert.assertTrue("robots.txt is empty", StringUtils.isNotBlank(body));
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, response);
    }

    @Test
    public void humansTxtIsPresentAndText() {
        String humansTxt = String.format("%s%s", TestUtils.getTestUrl(), Endpoint.HUMANS_TXT);
        HttpResponse<String> response = TestUtils.unirestGet(humansTxt);
        log.debug("Response: {}", response);
        if (response == null) return;

        Assert.assertEquals(200, response.getStatus());

        String body = response.getBody();
        Assert.assertTrue("humans.txt is empty", StringUtils.isNotBlank(body));
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, response);
    }

    @Test
    public void faviconIsPresentAndIcon() {
        String favIcon = String.format("%s%s", TestUtils.getTestUrl(), Endpoint.FAVICON_ICO);
        HttpResponse<String> response = TestUtils.unirestGet(favIcon);
        log.debug("Response: {}", response);
        if (response == null) return;

        Assert.assertEquals(200, response.getStatus());

        String body = response.getBody();
        Assert.assertTrue("favicon.ico is empty", StringUtils.isNotBlank(body));
        //in Spring boot 2 favicon has image/x-icon mimetype
        TestUtils.assertContentType(MimeType.IMAGE_X_ICON, response);
    }
}
