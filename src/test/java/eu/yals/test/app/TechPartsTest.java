package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.test.TestUtils;
import kong.unirest.HttpRequest;
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
@SuppressWarnings("unchecked")
@Slf4j
public class TechPartsTest extends UnirestTest {

    private static final String ALWAYS_NOT_FOUND_LOCATION = "/void/notFound";
    private static final String ALWAYS_NOT_FOUND_API_LOCATION = "/api/void/notFound";

    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void byDefaultAppReturnHtmlWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.TEXT_HTML, result);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML);

        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(406, result.getStatus());
    }

    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.FAIL_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);

        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void byDefaultAppReturnHtmlWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.FAIL_ENDPOINT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.TEXT_HTML, result);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(406, result.getStatus());
    }

    @Test
    public void robotsTxtIsPresentAndText() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ROBOTS_TXT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        log.debug("Response: {}", result);
        if (result == null) return;

        Assert.assertEquals(200, result.getStatus());

        String body = result.getBody();
        Assert.assertTrue("robots.txt is empty", StringUtils.isNotBlank(body));
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, result);
    }

    @Test
    public void humansTxtIsPresentAndText() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.HUMANS_TXT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);
        log.debug("Response: {}", result);
        if (result == null) return;

        Assert.assertEquals(200, result.getStatus());

        String body = result.getBody();
        Assert.assertTrue("humans.txt is empty", StringUtils.isNotBlank(body));
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, result);
    }

    @Test
    public void faviconIsPresentAndIcon() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.FAVICON_ICO);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);
        log.debug("Response: {}", result);
        if (result == null) return;

        Assert.assertEquals(200, result.getStatus());

        String body = result.getBody();
        Assert.assertTrue("favicon.ico is empty", StringUtils.isNotBlank(body));
        //in Spring boot 2 favicon has image/x-icon mimetype
        TestUtils.assertContentType(MimeType.IMAGE_X_ICON, result);
    }
}
