package io.kyberorg.yalsee.ui.pages.err.raw500;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.controllers.YalseeErrorController;
import io.kyberorg.yalsee.exception.RawLoopException;
import io.kyberorg.yalsee.exception.RawServerException;
import io.kyberorg.yalsee.exception.error.YalseeError;
import io.kyberorg.yalsee.ui.layouts.ServerErrorLayout;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@SpringComponent
@UIScope
@PageTitle("Yalsee: Error 500")
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
     * EntryPoint from {@link YalseeErrorController}.
     *
     * @param event     Vaadin Event with location, payload
     * @param parameter string goes after errors/500raw. We ignore it, because we use queryParams instead
     */
    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String parameter) {
        YalseeError yalseeError = errorUtils.getYalseeErrorFromEvent(event);
        if (!Objects.isNull(yalseeError)) {
            fillUIWithValuesFromError(yalseeError);
            event.rerouteToError(RawLoopException.class, Integer.toString(yalseeError.getHttpStatus()));
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
