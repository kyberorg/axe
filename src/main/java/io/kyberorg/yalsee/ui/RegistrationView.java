package io.kyberorg.yalsee.ui;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.core.YalseeLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Yalsee: Registration Page")
public class RegistrationView extends YalseeLayout {

    public RegistrationView() {
        setId(IDs.PAGE_ID);
    }

    public class IDs {
        public static final String PAGE_ID = "registerPage";
    }
}
