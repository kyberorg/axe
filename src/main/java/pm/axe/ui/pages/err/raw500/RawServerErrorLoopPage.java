package pm.axe.ui.pages.err.raw500;

import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import kong.unirest.HttpStatus;
import pm.axe.Endpoint;
import pm.axe.exception.RawLoopException;
import pm.axe.exception.RawServerException;
import pm.axe.ui.layouts.AxeBaseLayout;

/**
 * This View sends everything back to {@link RawServerErrorPage} and intended to be used only
 * within {@link RawServerErrorPage}.
 * Needed due to weird behavior of {@link BeforeEnterEvent#rerouteToError(Exception, String)}} method.
 *
 * @since 3.11
 */
@SpringComponent
@UIScope
@PageTitle("Raw Server Error Loop - Axe.pm")
@Route(value = Endpoint.TNT.RAW_SERVER_ERROR_LOOP)
public class RawServerErrorLoopPage extends AxeBaseLayout implements HasErrorParameter<RawLoopException> {
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<RawLoopException> parameter) {
        event.rerouteToError(RawServerException.class, parameter.getCustomMessage());
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
