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
public class TechPartsTest {

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
    public void humansTxtIsPresentAndText() throws Exception {
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
    public void faviconIsPresentAndIcon() throws Exception {
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
