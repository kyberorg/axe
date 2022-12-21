package pm.axe.ui.pages.user;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Login Page - Axe.pm")
public class LoginPage extends AxeBaseLayout {
    /**
     * Creates {@link LoginPage}.
     */
    public LoginPage() {
        add(new H2("Login Page will be here soon."));
    }
}
