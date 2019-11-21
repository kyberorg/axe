package eu.yals.ui.err;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.core.IdentGenerator;
import eu.yals.ui.AppView;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringComponent
@UIScope
@Route(value = Endpoint.NOT_FOUND_PAGE, layout = AppView.class)
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    Span text = new Span();

    public NotFoundView() {
        text.setText("404 - No such page found");
        add(text);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        boolean identNotFound = path.equals(Endpoint.SLASH_VAADIN);
        if (identNotFound) {
            if (AppUtils.clientWantsJson(VaadinRequest.getCurrent())) {
                VaadinResponse.getCurrent().setHeader(Header.LOCATION, Endpoint.NOT_FOUND_PAGE_FOR_API);
                return 302;
            }
            text.setText("404 - No such link found");
            return 404;
        }
        if (StringUtils.isBlank(path)) {
            event.rerouteTo("/");
            return 302;
        } else {
            boolean isIdentValid = path.matches(IdentGenerator.VALID_IDENT_PATTERN);
            if (isIdentValid) {
                List<String> p = new ArrayList<>();
                p.add(path);
                event.rerouteTo(Endpoint.SLASH_VAADIN, p);
                return 302;
            } else {
                //not a ident
                if (isApiRequest(path)) {
                    //api call
                    VaadinResponse.getCurrent().setHeader(Header.LOCATION, Endpoint.NOT_FOUND_PAGE_FOR_API);
                    return 302;
                } else {
                    VaadinResponse.getCurrent().setHeader(Header.LOCATION, Endpoint.NOT_FOUND_PAGE);
                    return 404;
                }
            }
        }
    }

    private boolean isApiRequest(String path) {
        return path.startsWith("api");
    }

}
