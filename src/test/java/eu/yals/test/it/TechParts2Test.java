package eu.yals.test.it;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import eu.yals.Endpoint;
import eu.yals.constants.MimeType;
import eu.yals.test.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;

import static org.junit.Assert.assertFalse;

@Slf4j
public class TechParts2Test {

    @Test
    public void robotsTxtIsPresentAndText() {
        String statusApiEndpoint = String.format("%s%s", TestUtils.getTestUrl(), Endpoint.ROBOTS_TXT);
        HttpResponse<String> apiResponse;
        try {
            apiResponse = Unirest.get(statusApiEndpoint).asString();
        } catch (Exception e) {
            log.error("Failed to Request API. Communication error", e);
            //MalformedURLException means configuration error
            assertFalse(e.getCause() instanceof MalformedURLException);
            return;
        }
        log.debug("API response: {}", apiResponse);

        Assert.assertEquals(200, apiResponse.getStatus());

        String body = apiResponse.getBody();
        Assert.assertTrue("robots.txt are empty", StringUtils.isNotBlank(body));
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, apiResponse);
    }

}
