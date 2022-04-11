package io.kyberorg.yalsee.ui.pages.err.raw500;

import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.exception.RawLoopException;
import io.kyberorg.yalsee.exception.RawServerException;
import io.kyberorg.yalsee.ui.layouts.YalseeLayout;

/**
 * This View sends everything back to {@link RawServerErrorPage} and intended to be used only
 * within {@link RawServerErrorPage}.
 * Needed due to weird behavior of {@link BeforeEnterEvent#rerouteToError(Exception, String)}} method.
 *
 * @since 3.11
 */
@SpringComponent
@UIScope
@PageTitle("Yalsee: Raw Server Error Loop View")
@Route(value = Endpoint.TNT.RAW_SERVER_ERROR_LOOP)
public class RawServerErrorLoopPage extends YalseeLayout implements HasErrorParameter<RawLoopException> {
    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<RawLoopException> parameter) {
        event.rerouteToError(RawServerException.class, parameter.getCustomMessage());
        return HttpCode.SERVER_ERROR;
    }
}
