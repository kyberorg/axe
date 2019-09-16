package ee.yals.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import ee.yals.Endpoint;
import ee.yals.controllers.internal.YalsController;
import ee.yals.core.IdentGenerator;
import ee.yals.json.LinkResponseJson;
import ee.yals.result.GetResult;
import ee.yals.services.LinkService;
import ee.yals.utils.AppUtils;
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
 * Handles {@link ee.yals.Endpoint#SLASH_BASE}
 *
 * @since 1.0
 */
@Controller
@Slf4j
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

        log.info(String.format("%s Got {\"Ident\": %s}", TAG, ident));
        if (StringUtils.isBlank(ident) || !ident.matches(IdentGenerator.VALID_IDENT_PATTERN)) {
            log.info(String.format("%s Got malformed request. Replying with 404. {\"Ident\": %s}", TAG, ident));
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
            log.debug(String.format("%s Searching for ident: '%s'", TAG, ident));
            String schema = request.getScheme() + "://";
            String url = schema + appUtils.getAPIHostPort() + Endpoint.LINK_API + ident;
            log.debug(String.format("%s Requesting API. URL: %s", TAG, url));
            apiResponse = Unirest.get(url).asString();
        } catch (Exception e) {
            String message = String.format("%s Exception while searching for link by ident. Ident: %s", TAG, ident);
            log.error(String.format("%s %s", TAG, message), e);
            return render500();
        }

        if (Objects.isNull(apiResponse)) {
            log.error(TAG + " No reply from API");
            return render500();
        }

        switch (apiResponse.getStatus()) {
            case 200:
                String link = extractLink(apiResponse);
                log.info(String.format("%s Got long URL. Redirecting to %s", TAG, link));
                return redirect(link);
            case 400:
                log.info(TAG + " Got malformed request. Replying with 404");
                return render404Ident();
            case 404:
                log.info(TAG + " No corresponding longURL found. Replying with 404");
                return render404Ident();
            case 500:
                log.info(TAG + " Got internal error. Replying with 500");
                return render500();
            default:
                log.info(String.format("%s Got unknown status: %d. I don't know how to handle it. " +
                        "Replying with 500", TAG, apiResponse.getStatus()));
                return render500();
        }
    }

    private String extractLink(HttpResponse<String> apiResponse) {
        LinkResponseJson linkJson = AppUtils.GSON.fromJson(apiResponse.getBody(), LinkResponseJson.class);
        return linkJson.getLink();
    }
}
