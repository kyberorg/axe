package ee.yals.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import ee.yals.Endpoint;
import ee.yals.controllers.internal.YalsController;
import ee.yals.json.LinkResponseJson;
import ee.yals.result.GetResult;
import ee.yals.services.LinkService;
import ee.yals.utils.AppUtils;
import ee.yals.utils.HostHelper;
import ee.yals.utils.IdentGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Handles {@link ee.yals.Endpoint#SLASH_BASE}
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
@Controller
public class SlashController extends YalsController {
    private static final Logger Log = Logger.getLogger(SlashController.class);

    @Autowired
    @Qualifier("dbStorage")
    private LinkService service;

    @RequestMapping(method = RequestMethod.GET,
            value = Endpoint.SLASH)
    public String slash(@PathVariable("ident") String ident, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        Log.debug("Got ident: " + ident);
        if (StringUtils.isBlank(ident) || !ident.matches(IdentGenerator.VALID_IDENT_PATTERN)) {
            return render404();
        }

        //This workaround for tests
        if (hasTestHeader()) {
            GetResult getResult = service.getLink(ident);
            if (getResult instanceof GetResult.Success) {
                return redirect(((GetResult.Success) getResult).getLink());
            } else {
                return render404Ident();
            }
        }

        HttpResponse<String> apiResponse;

        try {
            Log.info(String.format("Searching for ident. Ident %s", ident));
            String schema = request.getScheme() + "://";
            String url = schema + HostHelper.getHostFromRequest(request) + Endpoint.LINK_API + ident;
            Log.info(String.format("Requesting API. URL: %s", url));
            apiResponse = Unirest.get(url).asString();
        } catch (Exception e) {
            Log.error(String.format("Exception while searching for link by ident. Ident: %s", ident));
            Log.error(e);
            return render500();
        }

        if (Objects.isNull(apiResponse)) {
            Log.error("No reply from API");
            return render500();
        }

        switch (apiResponse.getStatus()) {
            case 200:
                String link = extractLink(apiResponse);
                Log.info(String.format("Got long URL. Redirecting to %s", link));
                return redirect(link);
            case 400:
                Log.info("Got malformed request. Replying with 404");
                return render404Ident();
            case 404:
                Log.info("No corresponding longURL found. Replying with 404");
                return render404Ident();
            case 500:
                Log.info("Got internal error. Replying with 500");
                return render500();
            default:
                Log.info(String.format("Got unknown status: %d. I don't know how to handle it. " +
                        "Replying with 500", apiResponse.getStatus()));
                return render500();
        }
    }

    private String extractLink(HttpResponse<String> apiResponse) {
        LinkResponseJson linkJson = AppUtils.GSON.fromJson(apiResponse.getBody(), LinkResponseJson.class);
        return linkJson.getLink();
    }
}
