package eu.yals.test.app;

import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.test.TestApp;
import eu.yals.test.TestUtils;
import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Tests, where we run application same ways in {@link eu.yals.test.ui.UITest}
 * and test by doing requests using {@link kong.unirest.Unirest}
 *
 * @since 2.5.1
 */
@Slf4j
public class UnirestTest {
    static final String TEST_URL = TestUtils.getTestUrl();
    public static final String TAG = "[Unirest]";

    @BeforeClass
    public static void setUp() {
        //application runs and accessible locally aka localhost
        System.setProperty(TestApp.Properties.TEST_RUN_MODE, TestApp.RunMode.LOCAL.name());

        Unirest.config().instrumentWith(requestSummary -> {
            long startNanos = System.nanoTime();
            return (responseSummary, exception) -> log.info("{} path: {} status: {} time: {}",
                    TAG,
                    requestSummary.getRawPath(),
                    responseSummary.getStatus(),
                    System.nanoTime() - startNanos);
        });
    }

    protected HttpResponse<String> uniGet(String endpoint) {
        endpoint = TestUtils.normalizeUrl(endpoint);
        GetRequest getRequest = Unirest.get(endpoint);
        logRequest(getRequest);
        try {
            HttpResponse<String> response = getRequest.asString();
            logResponse(response);
            return response;
        } catch (Exception e) {
            String errorMessage = "Failed to Request API. Communication error. Endpoint: " + endpoint;
            fail(errorMessage);
            //MalformedURLException means configuration error
            assertFalse(e.getCause() instanceof MalformedURLException);
            return null;
        }
    }

    protected HttpResponse<JsonNode> uniGetJson(String endpoint) {
        endpoint = TestUtils.normalizeUrl(endpoint);
        GetRequest getRequest = Unirest.get(endpoint);
        logRequest(getRequest);
        try {
            HttpResponse<JsonNode> response = getRequest.asJson();
            logResponse(response);
            return response;
        } catch (Exception e) {
            String errorMessage = "Failed to Request API. Communication error. Endpoint: " + endpoint;
            fail(errorMessage);
            //MalformedURLException means configuration error
            assertFalse(e.getCause() instanceof MalformedURLException);
            return null;
        }
    }

    protected HttpResponse<String> uniPost(String endpoint, String payload) {
        return uniPost(endpoint, payload, MimeType.APPLICATION_JSON);
    }

    protected HttpResponse<String> uniPost(String endpoint, String payload, String mimeType) {
        endpoint = TestUtils.normalizeUrl(endpoint);
        HttpRequestWithBody post = Unirest.post(endpoint);
        if (payload != null) { //only NULL is not accepted, sometimes there is need to send "" request
            post.body(payload);
        }
        if (StringUtils.isNotBlank(mimeType)) {
            post.header(Header.CONTENT_TYPE, mimeType);
        }
        logRequest(post);
        try {
            HttpResponse<String> response = post.asString();
            logResponse(response);
            return response;
        } catch (Exception e) {
            String errorMessage = "Failed to Request API. Communication error. Endpoint: " + endpoint;
            fail(errorMessage);
            //MalformedURLException means configuration error
            assertFalse(e.getCause() instanceof MalformedURLException);
            return null;
        }
    }

    private void logRequest(HttpRequest request) {
        StringBuilder reqLog = new StringBuilder("Request: ").append(App.NEW_LINE);
        reqLog.append(String.format("Request URL: %s", request.getUrl())).append(App.NEW_LINE);
        reqLog.append(String.format("Request method: %s", request.getHttpMethod().name())).append(App.NEW_LINE);
        if (request.getHeaders().size() > 0) {
            reqLog.append("Request headers: ").append(App.NEW_LINE);
            for (kong.unirest.Header header : request.getHeaders().all()) {
                reqLog.append(String.format("%s: %s", header.getName(), header.getValue())).append(App.NEW_LINE);
            }
        }
        log.info(String.format("%s %s", TAG, reqLog.toString()));
    }

    private void logResponse(HttpResponse response) {
        StringBuilder respLog = new StringBuilder("Response: ").append(App.NEW_LINE);
        respLog.append(String.format("Response status code: %s", response.getStatus())).append(App.NEW_LINE);
        respLog.append(String.format("Request body: %s", response.getBody())).append(App.NEW_LINE);
        if (response.getHeaders().size() > 0) {
            respLog.append("Request headers: ").append(App.NEW_LINE);
            for (kong.unirest.Header header : response.getHeaders().all()) {
                respLog.append(String.format("%s: %s", header.getName(), header.getValue())).append(App.NEW_LINE);
            }
        }
        log.info(String.format("%s %s", TAG, respLog.toString()));
    }
}
