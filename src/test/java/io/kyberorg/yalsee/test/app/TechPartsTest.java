package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.test.TestUtils;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Tech Parts and other non-standard locations
 *
 * @since 2.5.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class TechPartsTest extends UnirestTest {
    public static final String TAG = "[" + TechPartsTest.class.getSimpleName() + "]";

    private static final String ALWAYS_NOT_FOUND_LOCATION = "/void/notFound";
    private static final String ALWAYS_NOT_FOUND_API_LOCATION = "/api/void/notFound";

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

    @Test
    public void byDefaultAppReturnHtmlWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_LOCATION);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.TEXT_HTML, result);
    }

    @Test
    public void onApiRequestWithHeaderAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_JSON);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenNothingFound() {
        HttpRequest request = Unirest.get(TEST_URL + ALWAYS_NOT_FOUND_API_LOCATION)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML);

        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_406, result.getStatus());
    }

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

    @Test
    public void byDefaultAppReturnHtmlWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_ENDPOINT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.TEXT_HTML, result);
    }

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

    @Test
    public void onApiRequestWithoutHeadersAppReturnJsonWhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_API_ENDPOINT);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_500, result.getStatus());

        TestUtils.assertResponseBodyNotEmpty(result);
        TestUtils.assertContentType(MimeType.APPLICATION_JSON, result);
    }

    @Test
    public void onApiRequestWithNotJsonInAcceptHeaderAppReturns406WhenFailed() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.FAIL_API_ENDPOINT)
                .header(Header.ACCEPT, MimeType.APPLICATION_XML);
        HttpResponse<String> result = request.asString();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_406, result.getStatus());
    }

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

    @Test
    public void robotsTxtHasSitemapLink() {
        String[] bodyLineByLine = readRobotsLineByLine();
        assertNotNull(bodyLineByLine, "Failed to read robots.txt");

        String siteMapLine = null;
        for (String line: bodyLineByLine) {
           if(line.contains("Sitemap")) {
               siteMapLine = line;
               break;
           }
        }

        assertNotNull(siteMapLine,"No sitemap line found in robots.txt");
    }

    @Test
    public void sitemapLinkInRobotsTxtIfValid() {
        String[] bodyLineByLine = readRobotsLineByLine();
        assertNotNull(bodyLineByLine, "Failed to read robots.txt");

        String siteMapLine = null;
        for (String line: bodyLineByLine) {
            if(line.contains("Sitemap")) {
                siteMapLine = line;
                break;
            }
        }

        assertNotNull(siteMapLine,"No sitemap line found in robots.txt");
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
