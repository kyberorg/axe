package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.PASSWORD_RECOVERY_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Password Recovery Page")
public class PasswordRecoveryView extends Div implements HasUrlParameter<String> {
    private static final String DIRECT_MESSAGE = "Not intended for direct use";
    private static final String NO_PARAMS_MESSAGE = "Not intended for use without required parameters";

    private final YalseeLayout yalseeLayout = new YalseeLayout();
    private final Span banner = new Span();

    private Component coreLayout;

    private final YalseeFormLayout formLayout = new YalseeFormLayout();

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter String parameter) {
        if (requestHasNoParams(event)) {
            coreLayout = yalseeLayoutWithMessage(DIRECT_MESSAGE);
        } else if (isTokenParamsPresent(event)) {
            coreLayout = formLayout;
        } else {
            //no params
            coreLayout = yalseeLayoutWithMessage(NO_PARAMS_MESSAGE);
        }
        add(coreLayout);
    }

    private YalseeLayout yalseeLayoutWithMessage(final String message) {
        banner.setText(message);
        yalseeLayout.add(banner);
        return yalseeLayout;
    }

    private boolean requestHasNoParams(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().isEmpty();
    }

    private boolean isTokenParamsPresent(final BeforeEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        return queryParameters.getParameters().containsKey(App.Params.TOKEN);
    }
}
