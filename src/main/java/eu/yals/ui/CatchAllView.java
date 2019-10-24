package eu.yals.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@SpringComponent
@UIScope
@Route("vaadin")
public class CatchAllView extends VerticalLayout implements HasErrorParameter<NotFoundException> {
    public CatchAllView() {

    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        if (StringUtils.isBlank(path)) {
            event.rerouteTo("/");
        } else {
            boolean isIdentValid = path.matches(IdentGenerator.VALID_IDENT_PATTERN);
            if (isIdentValid) {
                event.rerouteTo(Endpoint.SLASH_BASE + "/" + path);
            } else {
                event.rerouteTo(Endpoint.NOT_FOUND_PAGE);
            }
        }

        return 307;
    }
}
