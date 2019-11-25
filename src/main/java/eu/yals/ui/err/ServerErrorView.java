package eu.yals.ui.err;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.json.YalsErrorJson;
import eu.yals.ui.AppView;
import eu.yals.utils.AppUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;
import java.util.Date;

@SpringComponent
@UIScope
@Route(value = Endpoint.VAADIN_ERROR_PAGE, layout = AppView.class)
public class ServerErrorView extends VerticalLayout implements HasErrorParameter<Exception>, HasUrlParameter<String> {

    private final H1 title = new H1("Hups...Something went wrong");
    private final Span when = new Span();
    private final Span what = new Span();
    private final Span message = new Span();

    public ServerErrorView() {
        init();
        add(title, when, what, message);
    }

    private void init() {
        when.setText(new Date().toString());
        what.setText("There was an unexpected error");
        message.setText("No message available");
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter.isEmpty()) {
            event.rerouteToError(Exception.class);
        } else {
            event.rerouteToError(Exception.class, parameter);
        }
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        if (parameter.hasCustomMessage()) {
            int status;
            YalsErrorJson errorJson;
            try {
                errorJson = extractErrorJsonFromParams(parameter);
                status = errorJson.getStatus();
                if (StringUtils.isNotBlank(errorJson.getTimestamp())) {
                    when.setText(errorJson.getTimestamp());
                }
                if (StringUtils.isNotBlank(errorJson.getError())) {
                    what.setText(errorJson.getError());
                }
                if (StringUtils.isNotBlank(errorJson.getMessage())) {
                    message.setText(errorJson.getMessage());
                }
            } catch (Exception e) {
                status = 500;
            }
            return status;
        } else {
            return 500;
        }
    }

    private YalsErrorJson extractErrorJsonFromParams(ErrorParameter parameter) {
        String encodedParam = parameter.getCustomMessage();
        String stringWithErrorJson = new String(Base64.getDecoder().decode(encodedParam));
        return AppUtils.GSON.fromJson(stringWithErrorJson, YalsErrorJson.class);
    }

}
