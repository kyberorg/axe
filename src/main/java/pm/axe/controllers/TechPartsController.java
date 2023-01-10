package pm.axe.controllers;

import com.beust.jcommander.Strings;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.HttpStatus;
import kong.unirest.MimeTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.utils.AppUtils;

import java.net.MalformedURLException;
import java.util.Date;

/**
 * Handles tech resources. Currently, only Fail endpoint for tests.
 *
 * @since 2.0
 */
@RequiredArgsConstructor
@Controller
public class TechPartsController {

    private static final double HIGH_PRIORITY = 1.0;
    private static final double APP_INFO_PAGE_PRIORITY = 0.7;
    private final AppUtils appUtils;

    /**
     * This endpoint meant to be used only in application tests for simulating application fails.
     *
     * @return always throws RuntimeException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = {Endpoint.ForTests.FAIL_ENDPOINT, Endpoint.ForTests.FAIL_API_ENDPOINT})
    public String iWillAlwaysFail() {
        throw new RuntimeException("I will always fail");
    }

    /**
     * Generates sitemap.xml dynamically.
     *
     * @param response response object to write status to
     * @return string with generated XML
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Static.SITEMAP_XML,
            produces = MimeTypes.XML)
    public @ResponseBody String getSitemap(final HttpServletResponse response) {
        String baseUrl = appUtils.getServerUrl();
        WebSitemapGenerator sitemapGenerator;

        try {
            sitemapGenerator = new WebSitemapGenerator(baseUrl);
            WebSitemapUrl mainPage = new WebSitemapUrl.Options(baseUrl + "/" +  Endpoint.UI.HOME_PAGE)
                    .lastMod(new Date()).priority(HIGH_PRIORITY).changeFreq(ChangeFreq.ALWAYS)
                    .build();

            WebSitemapUrl appInfo = new WebSitemapUrl.Options(baseUrl + "/" + Endpoint.UI.APP_INFO_PAGE)
                    .lastMod(new Date()).priority(APP_INFO_PAGE_PRIORITY).changeFreq(ChangeFreq.WEEKLY)
                    .build();

            sitemapGenerator.addUrl(mainPage).addUrl(appInfo);
        } catch (MalformedURLException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            throw new RuntimeException("Server URL is not configured - cannot generate Sitemap.xml");
        }

        response.setContentType(MimeTypes.XML);
        return Strings.join("", sitemapGenerator.writeAsStrings());
    }

    /**
     * Provides robots.txt in dynamic way.
     *
     * @return robots.txt content
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Static.ROBOTS_TXT, produces = MimeTypes.TXT)
    public @ResponseBody String getRobotsTxt() {
        StringBuilder builder = new StringBuilder();
        builder.append("User-agent: ").append("*").append(Axe.C.NEW_LINE);
        builder.append("Crawl-delay: ").append("1000").append(Axe.C.NEW_LINE);

        if (appUtils.areCrawlersAllowed()) {
            builder.append("Allow: ");
        } else {
           builder.append("Disallow: ");
        }
        builder.append("/").append(Axe.C.NEW_LINE);

        builder.append("Disallow: ").append("/vaadinServlet/").append(Axe.C.NEW_LINE);
        builder.append("Sitemap: ").append(appUtils.getServerUrl()).append(Endpoint.Static.SITEMAP_XML);
        return builder.toString();
    }

}
