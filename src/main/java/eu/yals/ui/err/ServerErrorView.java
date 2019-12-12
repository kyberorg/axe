package eu.yals.ui.err;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.controllers.YalsErrorController;
import eu.yals.json.YalsErrorJson;
import eu.yals.ui.AppView;
import eu.yals.utils.AppUtils;
import eu.yals.utils.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Date;
import java.util.Objects;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.ERROR_PAGE_500, layout = AppView.class)
public class ServerErrorView extends VerticalLayout implements HasErrorParameter<Exception>, HasUrlParameter<String> {
    private GuiUtils guiUtils;

    private final H1 title = new H1();
    private final Image image = new Image();

    private final Span when = new Span();
    private final Span what = new Span();
    private final Span message = new Span();
    private final Span trace = new Span();

    public ServerErrorView(GuiUtils guiUtils) {
        this.guiUtils = guiUtils;

        init();
        add(title, image, when, what, message, trace);
    }

    private void init() {
        image.setSrc("images/500.jpg");
        title.setText("Hups...Something went wrong");
        when.setText(new Date().toString());
        what.setText("There was an unexpected error");
        message.setText("");
        trace.setText("");
        trace.setEnabled(false);

    }

    /**
     * EntryPoint from {@link YalsErrorController}
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        YalsErrorJson yalsError = guiUtils.getYalsErrorFromEvent(event);
        if (Objects.isNull(yalsError)) {
            event.rerouteToError(Exception.class);
            return;
        }

        switch (yalsError.getStatus()) {
            case 404:
                event.rerouteToError(NotFoundException.class);
                return;
            case 503:
                event.rerouteToError(CannotCreateTransactionException.class);
                return;
            default:
                fillUIWithValuesFromError(yalsError);
                event.rerouteToError(Exception.class, Integer.toString(yalsError.getStatus()));
        }
    }

    /**
     * This method sets HTTP Code, based on payload, if no payload - status is 500
     *
     * @param event     same event as {@link #setParameter(BeforeEvent, String)}
     * @param parameter payload with status as String
     * @return http status
     */
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        return guiUtils.parseStatusFromErrorParameter(parameter, 500);
    }

    private void fillUIWithValuesFromError(YalsErrorJson yalsError) {
        if (StringUtils.isNotBlank(yalsError.getTimestamp())) {
            when.setText(yalsError.getTimestamp());
        }
        if (StringUtils.isNotBlank(yalsError.getError())) {
            what.setText(yalsError.getError());
        }
        //TODO only in DevMode
        if (StringUtils.isNotBlank(yalsError.getMessage())) {
            boolean notExceptionalSituation = yalsError.getMessage().equals(YalsErrorController.NO_EXCEPTION);
            if (notExceptionalSituation) {
                message.setEnabled(false);
            } else {
                message.setText(yalsError.getMessage());
            }
        }
        if (yalsError.getThrowable() != null) {
            trace.setText(AppUtils.stackTraceToString(yalsError.getThrowable()));
            trace.setEnabled(true);
        }
    }

}
