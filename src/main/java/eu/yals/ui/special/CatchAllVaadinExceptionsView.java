package eu.yals.ui.special;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.exception.error.YalsError;
import eu.yals.utils.ErrorUtils;
import eu.yals.utils.YalsErrorKeeper;

import javax.servlet.http.HttpServletRequest;

/**
 * Catches all unhandled exceptions from Vaadin Servlet
 */
@SpringComponent
@UIScope
public class CatchAllVaadinExceptionsView extends VerticalLayout implements HasErrorParameter<Exception> {

    private final YalsErrorKeeper yalsErrorKeeper;
    private final ErrorUtils errorUtils;

    public CatchAllVaadinExceptionsView(YalsErrorKeeper errorKeeper, ErrorUtils errorUtils) {
        this.yalsErrorKeeper = errorKeeper;
        this.errorUtils = errorUtils;
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<Exception> errorParameter) {
        Throwable cause = errorParameter.getCaughtException();
        String path = "/" + beforeEnterEvent.getLocation().getPathWithQueryParameters();

        ErrorUtils.Args args = ErrorUtils.ArgsBuilder.withException(cause)
                .addStatus(500)
                .addPath(path)
                .build();
        YalsError yalsError = errorUtils.convertExceptionToYalsError(args);

        String errorId = yalsErrorKeeper.send(yalsError);
        errorUtils.reportToBugsnag(yalsError);

        VaadinResponse.getCurrent().setHeader(Header.LOCATION,
                getMyHost() + "/" + Endpoint.UI.ERROR_PAGE_500 + "?" + App.Params.ERROR_ID + "=" + errorId);

        return 302;
    }

    private String getMyHost() {
        HttpServletRequest httpServletRequest = ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
        String fullUrl = httpServletRequest.getRequestURL().toString();
        String path = VaadinRequest.getCurrent().getPathInfo();

        //fullUrl - path = host
        return fullUrl.replace(path, "");
    }

}
