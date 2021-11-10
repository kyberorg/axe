package io.kyberorg.yalsee.ui.special;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.exception.error.YalseeError;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import io.kyberorg.yalsee.utils.ErrorUtils;
import io.kyberorg.yalsee.utils.YalseeErrorKeeper;
import lombok.AllArgsConstructor;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_302;
import static io.kyberorg.yalsee.constants.HttpCode.STATUS_500;

/**
 * Catches all unhandled exceptions from Vaadin Servlet.
 */
@AllArgsConstructor
@SpringComponent
@UIScope
public class CatchAllVaadinExceptionsView extends YalseeLayout implements HasErrorParameter<Exception> {

    private final YalseeErrorKeeper yalseeErrorKeeper;
    private final ErrorUtils errorUtils;

    @Override
    public int setErrorParameter(final BeforeEnterEvent beforeEnterEvent,
                                 final ErrorParameter<Exception> errorParameter) {
        Throwable cause = errorParameter.getCaughtException();
        String path = "/" + beforeEnterEvent.getLocation().getPathWithQueryParameters();

        ErrorUtils.Args args = ErrorUtils.ArgsBuilder.withException(cause)
                .addStatus(STATUS_500)
                .addPath(path)
                .build();
        YalseeError yalseeError = errorUtils.convertExceptionToYalseeError(args);

        String errorId = yalseeErrorKeeper.send(yalseeError);
        errorUtils.reportToBugsnag(yalseeError);

        VaadinResponse.getCurrent().setHeader(Header.LOCATION,
                "/" + Endpoint.UI.ERROR_PAGE_500 + "?" + App.Params.ERROR_ID + "=" + errorId);

        return STATUS_302;
    }
}
