package pm.axe.ui.pages.err.err500;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import kong.unirest.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.CannotCreateTransactionException;
import pm.axe.Endpoint;
import pm.axe.controllers.AxeErrorController;
import pm.axe.exception.GeneralServerException;
import pm.axe.exception.NeedForLoopException;
import pm.axe.exception.error.AxeError;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.ServerErrorLayout;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ErrorUtils;

import java.util.Objects;

@Slf4j
@SpringComponent
@UIScope
@PageTitle("Axe.pm: Error 500")
@Route(value = Endpoint.UI.ERROR_PAGE_500, layout = MainView.class)
@CssImport("./css/error_views.css")
public class ServerErrorPage extends ServerErrorLayout implements HasErrorParameter<GeneralServerException>,
        HasUrlParameter<String> {
    public static final String TAG = "[" + ServerErrorPage.class.getSimpleName() + "]";

    private final ErrorUtils errorUtils;

    /**
     * Creates {@link ServerErrorPage}.
     *
     * @param errorUtils error utils for actions with errors
     * @param appUtils   application utils
     */
    public ServerErrorPage(final ErrorUtils errorUtils, final AppUtils appUtils) {
        super(appUtils);
        this.errorUtils = errorUtils;
    }

    /**
     * EntryPoint from {@link AxeErrorController}.
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        AxeError axeError = errorUtils.getAxeErrorFromEvent(event);
        if (Objects.isNull(axeError)) {
            event.rerouteToError(NeedForLoopException.class, Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR));
            return;
        }

        switch (axeError.getHttpStatus()) {
            case HttpStatus.NOT_FOUND -> event.rerouteToError(NotFoundException.class);
            case HttpStatus.SERVICE_UNAVAILABLE -> event.rerouteToError(CannotCreateTransactionException.class);
            default -> {
                fillUIWithValuesFromError(axeError);
                event.rerouteToError(NeedForLoopException.class, Integer.toString(axeError.getHttpStatus()));
            }
        }
    }

    /**
     * This method sets HTTP Code, based on payload, if no payload - status is 500.
     *
     * @param event     same event as {@link #setParameter(BeforeEvent, String)}
     * @param parameter payload with status as String
     * @return http status
     */
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<GeneralServerException> parameter) {
        return errorUtils.parseStatusFromErrorParameter(parameter, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
