package pm.axe.ui.pages.err.raw500;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import pm.axe.Endpoint;
import pm.axe.constants.HttpCode;
import pm.axe.controllers.AxeErrorController;
import pm.axe.exception.RawLoopException;
import pm.axe.exception.RawServerException;
import pm.axe.exception.error.AxeError;
import pm.axe.ui.layouts.ServerErrorLayout;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ErrorUtils;

import java.util.Objects;

@Slf4j
@SpringComponent
@UIScope
@PageTitle("Error 500 Page - Axe.pm")
@Route(value = Endpoint.UI.RAW_ERROR_PAGE_500)
@CssImport("./css/error_views.css")
public class RawServerErrorPage extends ServerErrorLayout implements HasUrlParameter<String>,
        HasErrorParameter<RawServerException> {
    public static final String TAG = "[" + RawServerErrorPage.class.getSimpleName() + "]";

    private final ErrorUtils errorUtils;

    /**
     * Creates {@link RawServerErrorPage}.
     *
     * @param errorUtils error utils for actions with errors
     * @param appUtils   application utils
     */
    public RawServerErrorPage(final ErrorUtils errorUtils, final AppUtils appUtils) {
        super(appUtils);
        this.errorUtils = errorUtils;
    }

    /**
     * EntryPoint from {@link AxeErrorController}.
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500raw. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        AxeError axeError = errorUtils.getAxeErrorFromEvent(event);
        if (!Objects.isNull(axeError)) {
            fillUIWithValuesFromError(axeError);
            event.rerouteToError(RawLoopException.class, Integer.toString(axeError.getHttpStatus()));
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
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<RawServerException> parameter) {
        return errorUtils.parseStatusFromErrorParameter(parameter, HttpCode.SERVER_ERROR);
    }
}
