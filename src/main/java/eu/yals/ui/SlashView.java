package eu.yals.ui;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import eu.yals.json.LinkResponseJson;
import eu.yals.services.LinkService;
import eu.yals.ui.err.AppDownView;
import eu.yals.ui.err.IdentNotFoundView;
import eu.yals.ui.err.NotFoundView;
import eu.yals.ui.err.ServerErrorView;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Controller
@Route(Endpoint.SLASH_VAADIN)
public class SlashView extends VerticalLayout implements HasUrlParameter<String> {
    private static final String TAG = "[Vaadin Web]";

    private final LinkService service;
    private final AppUtils appUtils;

    public SlashView(@Qualifier("dbStorage") LinkService service, AppUtils appUtils) {
        this.service = service;
        this.appUtils = appUtils;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        String ident = parameter;
        log.info("{} Got {\"Ident\": {}}", TAG, ident);
        if (StringUtils.isBlank(ident) || !ident.matches(IdentGenerator.VALID_IDENT_PATTERN)) {
            log.info("{} Got malformed request. Replying with 404. {\"Ident\": {}}", TAG, ident);
            event.rerouteTo(NotFoundView.class);
            return;
        }

        /*//This workaround for tests
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
        }*/

        HttpResponse<String> apiResponse;
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        HttpServletRequest request = ((VaadinServletRequest) vaadinRequest).getHttpServletRequest();

        try {
            log.debug("{} Searching for ident: '{}'", TAG, ident);
            String schema = request.getScheme() + "://";
            String url = schema + appUtils.getAPIHostPort() + Endpoint.LINK_API + ident;
            log.debug("{} Requesting API. URL: {}", TAG, url);
            apiResponse = Unirest.get(url).asString();
        } catch (Exception e) {
            log.error("{} Exception while searching for link by ident. Ident: {}", TAG, ident, e);
            event.rerouteTo(ServerErrorView.class);
            return;
        }

        if (Objects.isNull(apiResponse)) {
            log.error("{} No reply from API", TAG);
            event.rerouteTo(ServerErrorView.class);
            return;
        }

        switch (apiResponse.getStatus()) {
            case 200:
                String link = extractLink(apiResponse);
                log.info("{} Got long URL. Redirecting to {}", TAG, link);
                event.forwardTo(link);
                return;
            case 400:
                log.info("{} Got malformed request. Replying with 404", TAG);
                event.rerouteTo(IdentNotFoundView.class);
                return;
            case 404:
                log.info("{} No corresponding longURL found. Replying with 404", TAG);
                event.rerouteTo(IdentNotFoundView.class);
                return;
            case 500:
                log.info("{} Got internal error. Replying with 500", TAG);
                event.rerouteTo(ServerErrorView.class);
                return;
            case 503:
                log.info("{} Database is DOWN. Replying with 503", TAG);
                event.rerouteTo(AppDownView.class);
                return;
            default:
                log.info("{} Got unknown status: {}. I don't know how to handle it. Replying with 500",
                        TAG, apiResponse.getStatus());
                event.rerouteTo(ServerErrorView.class);
        }
    }

    private String extractLink(HttpResponse<String> apiResponse) {
        LinkResponseJson linkJson = AppUtils.GSON.fromJson(apiResponse.getBody(), LinkResponseJson.class);
        return linkJson.getLink();
    }
}
