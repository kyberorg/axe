package pm.axe.ui.pages.err;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.constants.App;
import pm.axe.constants.Header;
import pm.axe.constants.HttpCode;
import pm.axe.exception.error.AxeError;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.utils.AxeErrorKeeper;
import pm.axe.utils.ErrorUtils;
import pm.axe.utils.RedirectLoopDetector;

/**
 * Catches all unhandled exceptions from Vaadin Servlet.
 */
@RequiredArgsConstructor
@SpringComponent
@UIScope
public class CatchAllVaadinExceptionsView extends AxeBaseLayout implements HasErrorParameter<Exception> {
    private final RedirectLoopDetector loopDetector;
    private final AxeErrorKeeper axeErrorKeeper;
    private final ErrorUtils errorUtils;

    @Override
    public int setErrorParameter(final BeforeEnterEvent beforeEnterEvent,
                                 final ErrorParameter<Exception> errorParameter) {
        Throwable cause = errorParameter.getCaughtException();
        String path = "/" + beforeEnterEvent.getLocation().getPathWithQueryParameters();

        ErrorUtils.Args args = ErrorUtils.ArgsBuilder.withException(cause)
                .addStatus(HttpCode.SERVER_ERROR)
                .addPath(path)
                .build();
        AxeError axeError = errorUtils.convertExceptionToAxeError(args);

        String errorId = axeErrorKeeper.send(axeError);
        errorUtils.reportToBugsnag(axeError);

        loopDetector.updateCounter();
        final String errorPageRoute = loopDetector.isLoopDetected()
                ? Endpoint.UI.RAW_ERROR_PAGE_500 : Endpoint.UI.ERROR_PAGE_500;

        VaadinResponse.getCurrent().setHeader(Header.LOCATION,
                "/" + errorPageRoute + "?" + App.Params.ERROR_ID + "=" + errorId);

        return HttpCode.TEMPORARY_REDIRECT;
    }
}
