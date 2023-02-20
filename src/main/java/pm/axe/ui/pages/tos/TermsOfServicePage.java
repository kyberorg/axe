package pm.axe.ui.pages.tos;

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
@Route(value = Endpoint.UI.TOS_PAGE, layout = MainView.class)
@PageTitle("Terms of Service - Axe.pm")
public class TermsOfServicePage extends AxeBaseLayout {
    /**
     * Creates {@link TermsOfServicePage}.
     */
    public TermsOfServicePage() {
        add(new Span("Here will be Axe Terms of Service"));
    }

}
