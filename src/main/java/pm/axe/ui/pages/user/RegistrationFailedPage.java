package pm.axe.ui.pages.user;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

@SpringComponent
@UIScope
@Route(value = Endpoint.UI.REGISTRATION_FAILED_PAGE, layout = MainView.class)
@PageTitle("Registration failed - Axe.pm")
public class RegistrationFailedPage extends AxeBaseLayout {
    public RegistrationFailedPage() {
        add(new Span("Registration failed"));
    }
}
