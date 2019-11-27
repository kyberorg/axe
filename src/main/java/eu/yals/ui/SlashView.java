package eu.yals.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import eu.yals.result.GetResult;
import eu.yals.services.LinkService;
import eu.yals.ui.err.AppDownView;
import eu.yals.ui.err.ServerErrorView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@UIScope
@Route(Endpoint.TNT.SLASH_IDENT)
public class SlashView extends VerticalLayout implements HasUrlParameter<String> {
    private static final String TAG = "[Vaadin Web]";

    private final LinkService linkService;

    public SlashView(LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        String ident = parameter;
        log.info("{} Got {\"Ident\": {}}", TAG, ident);
        if (StringUtils.isBlank(ident) || !ident.matches(IdentGenerator.VALID_IDENT_PATTERN)) {
            log.info("{} Got malformed request. Replying with 404. {\"Ident\": {}}", TAG, ident);
            event.rerouteToError(NotFoundException.class);
            return;
        }

        assert linkService != null;
        GetResult searchResult = linkService.getLink(ident);
        if (searchResult instanceof GetResult.Success) {
            String link = ((GetResult.Success) searchResult).getLink();
            log.info("{} Got long URL. Redirecting to {}", TAG, link);
            event.rerouteToError(ArithmeticException.class, link);
        } else if (searchResult instanceof GetResult.NotFound) {
            log.info("{} No corresponding longURL found. Replying with 404", TAG);
            event.rerouteToError(NotFoundException.class);
        } else if (searchResult instanceof GetResult.DatabaseDown) {
            log.info("{} Database is DOWN. Replying with 503", TAG);
            event.rerouteTo(AppDownView.class);
        } else {
            log.info("{} Got internal error. Replying with 500", TAG);
            event.rerouteTo(ServerErrorView.class);
        }
    }
}
