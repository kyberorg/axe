package io.kyberorg.yalsee.controllers;

import com.beust.jcommander.Strings;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.utils.AppUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * Handles tech resources. Currently only Fail endpoint for tests.
 *
 * @since 2.0
 */
@Controller
public class TechPartsController {

    private final AppUtils appUtils;

    public TechPartsController(AppUtils appUtils) {
        this.appUtils = appUtils;
    }

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
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Static.SITEMAP_XML, produces = MimeType.APPLICATION_XML)
    public @ResponseBody String getSitemap(HttpServletResponse response) {
        String baseUrl = appUtils.getServerUrl();
        WebSitemapGenerator sitemapGenerator;

        try {
            sitemapGenerator = new WebSitemapGenerator(baseUrl);
            WebSitemapUrl mainPage = new WebSitemapUrl.Options(baseUrl + "/" +  Endpoint.UI.HOME_PAGE)
                    .lastMod(new Date()).priority(1.0).changeFreq(ChangeFreq.ALWAYS)
                    .build();

            WebSitemapUrl appInfo = new WebSitemapUrl.Options(baseUrl + "/" + Endpoint.UI.APP_INFO_PAGE)
                    .lastMod(new Date()).priority(0.7).changeFreq(ChangeFreq.WEEKLY)
                    .build();

            sitemapGenerator.addUrl(mainPage).addUrl(appInfo);
        } catch (MalformedURLException e) {
            response.setStatus(500);
            throw new RuntimeException("Server URL is unconfigured - cannot generate Sitemap.xml");
        }

        response.setContentType(MimeType.APPLICATION_XML);
        return Strings.join("", sitemapGenerator.writeAsStrings());
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Static.ROBOTS_TXT, produces = MimeType.TEXT_PLAIN)
    public @ResponseBody String getRobotsTxt() {
        //FIXME impl
        return "";
    }

}
