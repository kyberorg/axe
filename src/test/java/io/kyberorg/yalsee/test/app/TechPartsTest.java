package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.test.utils.TestUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Tech Parts and other non-standard locations.
 *
 * @since 2.5.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class TechPartsTest extends UnirestTest {
    public static final String TAG = "[" + TechPartsTest.class.getSimpleName() + "]";

    private static final String ALWAYS_NOT_FOUND_LOCATION = "/void/notFound";
    private static final String ALWAYS_NOT_FOUND_API_LOCATION = "/api/void/notFound";

    /**
     * Request has accept header with {@link MimeType#APPLICATION_JSON} value = Reply with JSON.
     */
    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * Reply with {@link MimeType#TEXT_HTML} by default.
     */
    @Test
    public void byDefaultAppReturnHtmlWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.TEXT_HTML, result);
    }

    /**
     * Request to API with {@link MimeType#APPLICATION_JSON} header = Reply with JSON.
     */
    @Test
    public void onApiRequestWithJSONHeaderAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * Request to API without headers = Reply with JSON.
     */
    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * Request to API with other that {@link MimeType#APPLICATION_JSON} content type
     * (for example {@link MimeType#APPLICATION_XML}) = Reply with 406 (Not acceptable).
     */
    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML);

        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_406, result.getStatus());
    }

    /**
     * If client want {@link MimeType#APPLICATION_JSON}, we should reply with JSON even when we failed.
     */
    @Test
    public void ifRequestHasAcceptHeaderJsonAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);

        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * By default, app replies with {@link MimeType#TEXT_HTML}, when failed.
     */
    @Test
    public void byDefaultAppReturnHtmlWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_ENDPOINT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.TEXT_HTML, result);
    }

    /**
     * On API request with {@link MimeType#APPLICATION_JSON} header, App replies with JSON.
     */
    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * On API request without headers, App replies with JSON.
     */
    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_API_ENDPOINT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    /**
     * Request to API with other that {@link MimeType#APPLICATION_JSON} content type
     * (for example {@link MimeType#APPLICATION_XML}) = Reply with 406 (Not acceptable).
     */
    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_406, result.getStatus());
    }

    /**
     * Tests that robots.txt exists and content type is {@link MimeType#TEXT_PLAIN}.
     */
    @Test
    public void robotsTxtIsPresentAndText() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Static.ROBOTS_TXT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        log.debug("Response: {}", result);
        if (result == null) return;

        assertEquals(STATUS_200, result.getStatus());

        String body = result.getBody();
        assertTrue(StringUtils.isNotBlank(body), "robots.txt is empty");
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, result);
    }

    /**
     * Tests that humans.txt exists and content type is {@link MimeType#TEXT_PLAIN}.
     */
    @Test
    public void humansTxtIsPresentAndText() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Static.HUMANS_TXT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);
        log.debug("Response: {}", result);
        if (result == null) return;

        assertEquals(STATUS_200, result.getStatus());

        String body = result.getBody();
        assertTrue(StringUtils.isNotBlank(body), "humans.txt is empty");
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, result);
    }

    /**
     * Tests that favicon exists and icon.
     */
    @Test
    public void faviconIsPresentAndIcon() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Static.FAVICON_ICO);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);
        log.debug("Response: {}", result);
        if (result == null) return;

        assertEquals(STATUS_200, result.getStatus());

        String body = result.getBody();
        assertTrue(StringUtils.isNotBlank(body), "favicon.ico is empty");
        //in Spring boot 2.2 favicon transferred without Content-Type Header, so we have to check Content-Length instead
        TestUtils.assertContentNotEmpty(result);
    }

    /**
     * Tests that Sitemap is present and XML.
     */
    @Test
    public void sitemapXmlIsPresentAndXml() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Static.SITEMAP_XML);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        log.debug("Response: {}", result);
        if (result == null) return;

        assertEquals(STATUS_200, result.getStatus());

        String body = result.getBody();
        assertTrue(StringUtils.isNotBlank(body), "sitemap.xml is empty");
        TestUtils.assertContentType(MimeType.APPLICATION_XML, result);
    }

    /**
     * Tests that robots.txt has link to Sitemap file.
     */
    @Test
    public void robotsTxtHasSitemapLink() {
        String[] bodyLineByLine = readRobotsLineByLine();
        assertNotNull(bodyLineByLine, "Failed to read robots.txt");

        String siteMapLine = null;
        for (String line: bodyLineByLine) {
           if (line.contains("Sitemap")) {
               siteMapLine = line;
               break;
           }
        }

        assertNotNull(siteMapLine, "No sitemap line found in robots.txt");
    }

    /**
     * Tests that link to Sitemap in robots.txt is valid (has word 'Sitemap' and points to correct location).
     */
    @Test
    public void sitemapLinkInRobotsTxtIsValid() {
        String[] bodyLineByLine = readRobotsLineByLine();
        assertNotNull(bodyLineByLine, "Failed to read robots.txt");

        String siteMapLine = null;
        for (String line: bodyLineByLine) {
            if (line.contains("Sitemap")) {
                siteMapLine = line;
                break;
            }
        }

        assertNotNull(siteMapLine, "No sitemap line found in robots.txt");
        assertTrue(siteMapLine.contains("sitemap.xml"),
                "Sitemap line is invalid: points to wrong location: " + siteMapLine);
        assertTrue(siteMapLine.contains(TEST_URL), "Sitemap line doesn't point to Test App URL");
    }

    private String[] readRobotsLineByLine() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Static.ROBOTS_TXT);
        HttpResponse<String> result = request.asString();

        log.debug("Response: {}", result);
        if (result == null) return null;

        assertEquals(STATUS_200, result.getStatus());

        String body = result.getBody();
        return body.split(App.NEW_LINE);
    }
}
