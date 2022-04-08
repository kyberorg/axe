package io.kyberorg.yalsee.ui.err;

import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.GeneralServerException;
import io.kyberorg.yalsee.exception.NeedForLoopException;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

/**
 * This View sends everything back to {@link ServerErrorPage} and intended to be used only
 * within {@link ServerErrorPage}.
 * Needed due to weird behavior of {@link BeforeEnterEvent#rerouteToError(Exception, String)}} method.
 *
 * @since 2.7.4
 */
@SpringComponent
@UIScope
@PageTitle("Yalsee: Server Error Loop View")
@Route(value = Endpoint.TNT.SERVER_ERROR_LOOP, layout = MainView.class)
public class ServerErrorLoopPage extends YalseeLayout implements HasErrorParameter<NeedForLoopException> {
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<NeedForLoopException> parameter) {
        event.rerouteToError(GeneralServerException.class, parameter.getCustomMessage());
        return HttpCode.SERVER_ERROR;
    }
}
