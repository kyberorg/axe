package eu.yals.test.it;

import com.mashape.unirest.http.HttpResponse;
import eu.yals.Endpoint;
import eu.yals.constants.MimeType;
import eu.yals.test.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

@Slf4j
public class TechParts2Test {

    @Test
    public void robotsTxtIsPresentAndText() {
        String robotsTxt = String.format("%s%s", TestUtils.getTestUrl(), Endpoint.ROBOTS_TXT);
        HttpResponse<String> apiResponse = TestUtils.unirestGet(robotsTxt);
        log.debug("API response: {}", apiResponse);
        if (apiResponse == null) return;

        Assert.assertEquals(200, apiResponse.getStatus());

        String body = apiResponse.getBody();
        Assert.assertTrue("robots.txt are empty", StringUtils.isNotBlank(body));
        TestUtils.assertContentType(MimeType.TEXT_PLAIN, apiResponse);
    }

}
