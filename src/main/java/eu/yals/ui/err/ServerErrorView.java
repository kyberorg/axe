package eu.yals.ui.err;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.controllers.YalsErrorController;
import eu.yals.json.YalsErrorJson;
import eu.yals.ui.AppView;
import eu.yals.utils.YalsErrorKeeper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.ERROR_PAGE_500, layout = AppView.class)
public class ServerErrorView extends VerticalLayout implements HasErrorParameter<Exception>, HasUrlParameter<String> {
    private YalsErrorKeeper errorKeeper;

    private final H1 title = new H1();
    private final Span when = new Span();
    private final Span what = new Span();
    private final Span message = new Span();

    public ServerErrorView(YalsErrorKeeper errorKeeper) {
        this.errorKeeper = errorKeeper;

        init();
        add(title, when, what, message);
    }

    private void init() {
        title.setText("Hups...Something went wrong");
        when.setText(new Date().toString());
        what.setText("There was an unexpected error");
        message.setText("No message available");
    }

    /**
     * EntryPoint from {@link YalsErrorController}
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        YalsErrorJson yalsError = getYalsError(event);
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
        int status;
        if (parameter != null && parameter.hasCustomMessage()) {
            String statusString = parameter.getCustomMessage();
            try {
                status = Integer.parseInt(statusString);
            } catch (Exception e) {
                status = 500;
            }
        } else {
            status = 500;
        }
        return status;
    }

    private YalsErrorJson getYalsError(BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        if (queryParameters.getParameters().isEmpty()) return null;
        boolean errorIdKeyIsPresent = queryParameters.getParameters().containsKey(App.Params.ERROR_ID);
        if (!errorIdKeyIsPresent) return null;

        List<String> errorIdValues = queryParameters.getParameters().get(App.Params.ERROR_ID);
        boolean errorIdKeyHasSingleValue = errorIdValues.size() == 1;
        if (!errorIdKeyHasSingleValue) return null;

        String errorId = errorIdValues.get(0);
        Optional<YalsErrorJson> yalsErrorOptional = errorKeeper.get(errorId);
        return yalsErrorOptional.orElse(null);
    }

    private void fillUIWithValuesFromError(YalsErrorJson yalsError) {
        if (StringUtils.isNotBlank(yalsError.getTimestamp())) {
            when.setText(yalsError.getTimestamp());
        }
        if (StringUtils.isNotBlank(yalsError.getError())) {
            what.setText(yalsError.getError());
        }
        if (StringUtils.isNotBlank(yalsError.getMessage())) {
            boolean notExceptionalSituation = yalsError.getMessage().equals(YalsErrorController.NO_EXCEPTION);
            if (notExceptionalSituation) {
                message.setEnabled(false);
            } else {
                message.setText(yalsError.getMessage());
            }
        }
    }

}
