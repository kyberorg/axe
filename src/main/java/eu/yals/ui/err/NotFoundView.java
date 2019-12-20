package eu.yals.ui.err;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringComponent
@UIScope
@PageTitle("Yals: Error 404")
@Route(value = Endpoint.UI.PAGE_404, layout = AppView.class)
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private final H1 title = new H1();
    private final Span subTitle = new Span();
    private final Image image = new Image();

    public NotFoundView() {
        init();
        add(title, subTitle, image);
        this.setAlignItems(Alignment.CENTER);
    }

    private void init() {
        title.setText("404 - No Such Page Exception");
        subTitle.setText("We don't have such page at our site. Really.");

        image.setSrc("images/404.jpg");
        image.setAlt("Error 404 Image");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        boolean identNotFound = path.equals(Endpoint.TNT.SLASH_IDENT);
        if (identNotFound) {
            if (AppUtils.clientWantsJson(VaadinRequest.getCurrent())) {
                VaadinResponse.getCurrent().setHeader(Header.LOCATION, api404Endpoint(event));
                return 302;
            } else {
                subTitle.setText("We don't have long link that match your short link. " +
                        "Make sure you copypasted it fully and without extra characters");
                return 404;
            }
        }
        if (StringUtils.isBlank(path)) {
            event.rerouteTo("/");
            return 302;
        } else {
            boolean isIdentValid = path.matches(IdentGenerator.VALID_IDENT_PATTERN);
            if (isIdentValid) {
                List<String> param = new ArrayList<>();
                param.add(path);
                event.rerouteTo(Endpoint.TNT.SLASH_IDENT, param);
                return 302;
            } else {
                if (AppUtils.clientWantsJson(VaadinRequest.getCurrent())) {
                    VaadinResponse.getCurrent().setHeader(Header.LOCATION, Endpoint.Api.PAGE_404);
                    return 302;
                }
                //not a ident
                if (isApiRequest(path)) {
                    //api call
                    VaadinResponse.getCurrent().setHeader(Header.LOCATION, api404Endpoint(event));
                    return 302;
                } else {
                    VaadinResponse.getCurrent().setHeader(Header.LOCATION, Endpoint.UI.PAGE_404);
                    return 404;
                }
            }
        }
    }

    private boolean isApiRequest(String path) {
        return path.startsWith("api");
    }

    private String api404Endpoint(BeforeEnterEvent event) {
        String method = VaadinRequest.getCurrent().getMethod();
        String path = event.getLocation().getPath();

        try {
            return String.format("%s?method=%s&path=%s", Endpoint.Api.PAGE_404, method,
                    URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return Endpoint.Api.PAGE_404;
        }
    }

}
