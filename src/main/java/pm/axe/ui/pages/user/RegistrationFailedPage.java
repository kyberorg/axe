package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

import java.util.stream.Stream;

/**
 * Page, which shown when confirmation performed by {@link ConfirmationView} fails.
 */
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.REGISTRATION_FAILED_PAGE, layout = MainView.class)
@PageTitle("Registration failed - Axe.pm")
public class RegistrationFailedPage extends AxeBaseLayout {
    private final H2 title = new H2("Registration failed");
    private final Span linkNotValidLine = new Span("The confirmation link is no longer valid.");
    private final Span expiredLinkLine = new Span("It may have been used already or it may have expired.");
    private Button loginButton;

    /**
     * Creates {@link RegistrationFailedPage}.
     */
    public RegistrationFailedPage() {
        init();
        applyStyle();

        add(title, linkNotValidLine, expiredLinkLine, loginButton);
    }

    private void init() {
        loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addClickListener(this::onLoginButtonClicked);
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private void applyStyle() {
        Stream<? extends HasStyle> elements = Stream.of(title, linkNotValidLine, expiredLinkLine, loginButton);
        elements.forEach(e -> e.getStyle().set(Axe.CSS.ALIGN_SELF, Axe.C.CENTER));
    }

    private void onLoginButtonClicked(final ClickEvent<Button> buttonClickEvent) {
        loginButton.getUI().ifPresent(ui -> ui.navigate(LoginPage.class));
    }
}
