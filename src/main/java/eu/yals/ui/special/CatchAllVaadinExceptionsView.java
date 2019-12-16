package eu.yals.ui.special;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import eu.yals.Endpoint;
import eu.yals.YalsException;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.json.YalsErrorJson;
import eu.yals.utils.YalsErrorKeeper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CatchAllVaadinExceptionsView extends VerticalLayout implements HasErrorParameter<Exception> {

    private YalsErrorKeeper yalsErrorKeeper;

    public CatchAllVaadinExceptionsView(YalsErrorKeeper errorKeeper) {
        this.yalsErrorKeeper = errorKeeper;
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<Exception> errorParameter) {
        YalsErrorJson errorJson = YalsErrorJson.withMessage(errorParameter.getCaughtException().getMessage());
        errorJson.setThrowable(errorParameter.getCaughtException());
        String errorId = yalsErrorKeeper.send(errorJson);

        VaadinResponse.getCurrent().setHeader(Header.LOCATION,
                getMyHost() + Endpoint.UI.ERROR_PAGE_500 + "?" + App.Params.ERROR_ID + "=" + errorId);

        return 302;
    }

    private String getMyHost() {
        //TODO might be buggy
        HttpServletRequest httpServletRequest = ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
        return httpServletRequest.getRequestURL().toString();
    }

}
