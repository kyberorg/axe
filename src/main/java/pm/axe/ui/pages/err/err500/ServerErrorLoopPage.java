package pm.axe.ui.pages.err.err500;

import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.constants.HttpCode;
import pm.axe.exception.GeneralServerException;
import pm.axe.exception.NeedForLoopException;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

/**
 * This View sends everything back to {@link ServerErrorPage} and intended to be used only
 * within {@link ServerErrorPage}.
 * Needed due to weird behavior of {@link BeforeEnterEvent#rerouteToError(Exception, String)}} method.
 *
 * @since 2.7.4
 */
@SpringComponent
@UIScope
@PageTitle("Server Error Loop Page - Axe.pm")
@Route(value = Endpoint.TNT.SERVER_ERROR_LOOP, layout = MainView.class)
public class ServerErrorLoopPage extends AxeBaseLayout implements HasErrorParameter<NeedForLoopException> {
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<NeedForLoopException> parameter) {
        event.rerouteToError(GeneralServerException.class, parameter.getCustomMessage());
        return HttpCode.SERVER_ERROR;
    }
}
