package io.kyberorg.yalsee.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeFormLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE + "f", layout = MainView.class)
@PageTitle("Yalsee: Debug Page")
public class FDebugView extends YalseeFormLayout {

    /**
     * Creates {@link FDebugView}.
     */
    public FDebugView() {
        setId(FDebugView.class.getSimpleName());
        /*add(new H2("Debug Page"));
        add(new Text("Ready to debug something..."));
        */
        addFormFields(new H2("qwertrytyutdddddddddddirit"));
    }
}
