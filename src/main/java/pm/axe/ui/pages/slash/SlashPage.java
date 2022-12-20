package pm.axe.ui.pages.slash;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.UIScope;
import kong.unirest.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.core.IdentGenerator;
import pm.axe.exception.IdentNotFoundException;
import pm.axe.exception.NeedForRedirectException;
import pm.axe.exception.PageNotFoundException;
import pm.axe.result.OperationResult;
import pm.axe.services.LinkService;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.ui.pages.err.appdown.AppDownPage;
import pm.axe.ui.pages.err.err500.ServerErrorPage;
import pm.axe.utils.AppUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Controller
@UIScope
public class SlashPage extends AxeBaseLayout implements HasErrorParameter<NotFoundException> {
    private static final String TAG = "[" + SlashPage.class.getSimpleName() + "]";

    private final AppUtils appUtils;
    private final LinkService linkService;


    /**
     * Handles every unmatched route within Application.
     *
     * @param event     Vaadin's event
     * @param parameter optional params that can be sent during redirect. We don't use it here.
     * @return int with HTTP Code
     */
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<NotFoundException> parameter) {
        String route = event.getLocation().getPath();
        if (isIdent(route)) {
            assert linkService != null;
            String ident = appUtils.dropRedirectPageBypassSymbolFrom(route);
            OperationResult searchResult = linkService.getLinkWithIdent(ident);

            if (searchResult.ok()) {
                String link = searchResult.getStringPayload();
                log.info("{} Got long URL. Redirecting to {}", TAG, link);
                event.rerouteToError(NeedForRedirectException.class,
                        route + Axe.C.URL_SAFE_SEPARATOR + link);
            } else if (searchResult.getResult().equals(OperationResult.ELEMENT_NOT_FOUND)) {
                log.info("{} No corresponding longURL found. Replying with 404. {\"Ident\": {}}", TAG, route);
                rerouteTo404(route, event, Target.IDENT_NOT_FOUND);
            } else if (searchResult.getResult().equals(OperationResult.SYSTEM_DOWN)) {
                log.info("{} Database is DOWN. Replying with 503", TAG);
                event.rerouteTo(AppDownPage.class);
            } else {
                log.info("{} Got internal error. Replying with 500", TAG);
                event.rerouteTo(ServerErrorPage.class);
            }
        } else {
            log.info("{} Page not found. Replying with 404. {\"Unknown Route\": {}}", TAG, route);
            rerouteTo404(route, event, Target.PAGE_NOT_FOUND);
        }

        return HttpStatus.TEMPORARY_REDIRECT;
    }

    private boolean isIdent(final String route) {
        String ident = appUtils.dropRedirectPageBypassSymbolFrom(route);
        return ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
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

    private void rerouteTo404(final String route, final BeforeEnterEvent event, final Target target) {
        if (isApiRequest(route) || AppUtils.clientWantsJson(VaadinRequest.getCurrent())) {
            VaadinResponse.getCurrent().setHeader(Axe.Headers.LOCATION, api404Endpoint(event));
        } else {
            if (target == Target.IDENT_NOT_FOUND) {
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
