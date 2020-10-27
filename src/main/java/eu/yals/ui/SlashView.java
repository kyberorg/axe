package eu.yals.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.core.IdentGenerator;
import eu.yals.exception.IdentNotFoundException;
import eu.yals.exception.NeedForRedirectException;
import eu.yals.exception.PageNotFoundException;
import eu.yals.result.GetResult;
import eu.yals.services.LinkService;
import eu.yals.ui.err.AppDownView;
import eu.yals.ui.err.ServerErrorView;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static eu.yals.constants.HttpCode.STATUS_302;

@Slf4j
@Controller
@UIScope
public class SlashView extends VerticalLayout implements HasErrorParameter<NotFoundException> {
    private static final String TAG = "[" + SlashView.class.getSimpleName() + "]";

    private final LinkService linkService;

    /**
     * Creates {@link SlashView}.
     *
     * @param linkService service to work with DB
     */
    public SlashView(final LinkService linkService) {
        this.linkService = linkService;
    }


    /**
     * Handles every unmatched route within Application.
     *
     * @param event     Vaadin's event
     * @param parameter optional params that can be send during redirect. We don't use it here.
     * @return int with HTTP Code
     */
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String route = event.getLocation().getPath();
        if(isIdent(route)) {
            assert linkService != null;
            GetResult searchResult = linkService.getLink(route);
            if (searchResult instanceof GetResult.Success) {
                String link = ((GetResult.Success) searchResult).getLink();
                log.info("{} Got long URL. Redirecting to {}", TAG, link);
                event.rerouteToError(NeedForRedirectException.class, link);
            } else if (searchResult instanceof GetResult.NotFound) {
                log.info("{} No corresponding longURL found. Replying with 404. {\"Ident\": {}}", TAG, route);
                rerouteTo404(route, event, Target.IDENT_NOT_FOUND);
            } else if (searchResult instanceof GetResult.DatabaseDown) {
                log.info("{} Database is DOWN. Replying with 503", TAG);
                event.rerouteTo(AppDownView.class);
            } else {
                log.info("{} Got internal error. Replying with 500", TAG);
                event.rerouteTo(ServerErrorView.class);
            }
        } else {
            log.info("{} Page not found. Replying with 404. {\"Unknown Route\": {}}", TAG, route);
            rerouteTo404(route, event, Target.PAGE_NOT_FOUND);
        }

        return STATUS_302;
    }

    private boolean isIdent(String route) {
        return route.matches(IdentGenerator.VALID_IDENT_PATTERN);
    }

    private boolean isApiRequest(final String path) {
        return path.startsWith("api");
    }

    private String api404Endpoint(final BeforeEnterEvent event) {
        String method = VaadinRequest.getCurrent().getMethod();
        String path = event.getLocation().getPath();

        return String.format("%s?method=%s&path=%s", Endpoint.Api.PAGE_404, method,
                URLEncoder.encode(path, StandardCharsets.UTF_8));
    }

    private void rerouteTo404(String route, BeforeEnterEvent event, Target target) {
        if (isApiRequest(route) || AppUtils.clientWantsJson(VaadinRequest.getCurrent())) {
            VaadinResponse.getCurrent().setHeader(Header.LOCATION, api404Endpoint(event));
        } else {
            if(target == Target.IDENT_NOT_FOUND) {
                event.rerouteToError(IdentNotFoundException.class);
            } else {
                event.rerouteToError(PageNotFoundException.class);
            }
        }
    }

    enum Target {
        IDENT_NOT_FOUND,
        PAGE_NOT_FOUND
    }
}
