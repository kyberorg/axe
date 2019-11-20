package eu.yals.controllers;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NotFoundLoc implements HasErrorParameter<NotFoundException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        if (StringUtils.isBlank(path)) {
            event.rerouteTo("/");
        } else {
            boolean isIdentValid = path.matches(IdentGenerator.VALID_IDENT_PATTERN);
            if (isIdentValid) {
                List<String> p = new ArrayList<>();
                p.add(path);
                event.rerouteTo(Endpoint.SLASH_VAADIN, p);
            } else {
                //not a ident
                if (isApiRequest(path)) {
                    //api call
                    event.rerouteTo("aaa");
                } else {
                    event.rerouteTo("404");
                    return 404;
                }
            }
        }

        return 302;
    }

    private boolean isApiRequest(String path) {
        return path.startsWith("api");
    }
}
