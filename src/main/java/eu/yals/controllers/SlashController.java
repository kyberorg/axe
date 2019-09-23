package eu.yals.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import eu.yals.Endpoint;
import eu.yals.controllers.internal.YalsController;
import eu.yals.core.IdentGenerator;
import eu.yals.json.LinkResponseJson;
import eu.yals.result.GetResult;
import eu.yals.services.LinkService;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Handles {@link Endpoint#SLASH_BASE}
 *
 * @since 1.0
 */
@Slf4j
@Controller
public class SlashController extends YalsController {
    private static final String TAG = "[Web]";

    private final LinkService service;
    private final AppUtils appUtils;

    public SlashController(@Qualifier("dbStorage") LinkService service, AppUtils appUtils) {
        this.service = service;
        this.appUtils = appUtils;
    }

    @RequestMapping(method = RequestMethod.GET,
            value = Endpoint.SLASH)
    public String slash(@PathVariable("ident") String ident, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        log.info("{} Got {\"Ident\": {}}", TAG, ident);
        if (StringUtils.isBlank(ident) || !ident.matches(IdentGenerator.VALID_IDENT_PATTERN)) {
            log.info("{} Got malformed request. Replying with 404. {\"Ident\": {}}", TAG, ident);
            return render404();
        }

        //This workaround for tests
        if (hasTestHeader()) {
            GetResult getResult = service.getLink(ident);
            if (getResult instanceof GetResult.Success) {
                return redirect(((GetResult.Success) getResult).getLink());
            } else if (getResult instanceof GetResult.NotFound) {
                return render404Ident();
            } else if (getResult instanceof GetResult.DatabaseDown) {
                return render503();
            } else {
                return render500();
            }
        }

        HttpResponse<String> apiResponse;

        try {
            log.debug("{} Searching for ident: '{}'", TAG, ident);
            String schema = request.getScheme() + "://";
            String url = schema + appUtils.getAPIHostPort() + Endpoint.LINK_API + ident;
            log.debug("{} Requesting API. URL: {}", TAG, url);
            apiResponse = Unirest.get(url).asString();
        } catch (Exception e) {
            log.error("{} Exception while searching for link by ident. Ident: {}", TAG, ident, e);
            return render500();
        }

        if (Objects.isNull(apiResponse)) {
            log.error("{} No reply from API", TAG);
            return render500();
        }

        switch (apiResponse.getStatus()) {
            case 200:
                String link = extractLink(apiResponse);
                log.info("{} Got long URL. Redirecting to {}", TAG, link);
                return redirect(link);
            case 400:
                log.info("{} Got malformed request. Replying with 404", TAG);
                return render404Ident();
            case 404:
                log.info("{} No corresponding longURL found. Replying with 404", TAG);
                return render404Ident();
            case 500:
                log.info("{} Got internal error. Replying with 500", TAG);
                return render500();
            case 503:
                log.info("{} Database is DOWN. Replying with 503", TAG);
                return render503();
            default:
                log.info("{} Got unknown status: {}. I don't know how to handle it. Replying with 500",
                        TAG, apiResponse.getStatus());
                return render500();
        }
    }

    private String extractLink(HttpResponse<String> apiResponse) {
        LinkResponseJson linkJson = AppUtils.GSON.fromJson(apiResponse.getBody(), LinkResponseJson.class);
        return linkJson.getLink();
    }
}
