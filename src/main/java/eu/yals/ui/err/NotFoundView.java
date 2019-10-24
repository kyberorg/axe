package eu.yals.ui.err;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringComponent
@UIScope
@Route("404")
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

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
                add(new Text("404 - No such page"));
                return 404;
            }
        }

        return 302;
    }
}
