package eu.yals.ui.err;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.yals.Endpoint;
import eu.yals.exception.GeneralServerException;
import eu.yals.exception.NeedForLoopException;
import eu.yals.ui.AppView;

/**
 * This View sends everything back to {@link eu.yals.ui.err.ServerErrorView} and intended to be used only
 * within {@link eu.yals.ui.err.ServerErrorView}.
 * Needed due to weird behavior of {@link BeforeEnterEvent#rerouteToError(Exception, String)}} method.
 *
 * @since 2.7.4
 */
@SpringComponent
@UIScope
@PageTitle("Yals: Server Error Loop View")
@Route(value = Endpoint.TNT.SERVER_ERROR_LOOP, layout = AppView.class)
public class ServerErrorLoopView extends Div implements HasErrorParameter<NeedForLoopException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NeedForLoopException> parameter) {
        event.rerouteToError(GeneralServerException.class, parameter.getCustomMessage());
        return 500;
    }
}
